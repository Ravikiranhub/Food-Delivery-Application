package com.foodhub.service;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.order.OrderRequest;
import com.foodhub.dto.order.OrderResponse;
import com.foodhub.dto.order.OrderStatusHistoryResponse;
import com.foodhub.dto.order.UpdateOrderStatusRequest;
import com.foodhub.entity.Address;
import com.foodhub.entity.Cart;
import com.foodhub.entity.Coupon;
import com.foodhub.entity.CustomerOrder;
import com.foodhub.entity.Delivery;
import com.foodhub.entity.OrderItem;
import com.foodhub.entity.OrderStatusHistory;
import com.foodhub.entity.Payment;
import com.foodhub.entity.User;
import com.foodhub.enums.DeliveryStatus;
import com.foodhub.enums.OrderStatus;
import com.foodhub.enums.PaymentMethod;
import com.foodhub.enums.PaymentStatus;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.enums.RoleName;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.exception.InvalidOrderStatusTransitionException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.RestaurantNotAvailableException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.OrderMapper;
import com.foodhub.repository.AddressRepository;
import com.foodhub.repository.CartRepository;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.DeliveryRepository;
import com.foodhub.repository.OrderStatusHistoryRepository;
import com.foodhub.repository.PaymentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("30.00");

    private final CurrentUserService currentUserService;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStatusHistoryRepository historyRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        User customer = currentUserService.getCurrentUser();
        Cart cart = cartService.getOrCreateCart(customer);
        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderException("Cart is empty");
        }
        if (cart.getRestaurant() == null) {
            throw new InvalidOrderException("Cart restaurant is missing");
        }
        if (cart.getRestaurant().getStatus() != RestaurantStatus.APPROVED || !cart.getRestaurant().isOpen()) {
            throw new RestaurantNotAvailableException("Restaurant is not accepting orders right now");
        }

        Address address = addressRepository.findByIdAndUserId(request.addressId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found"));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (var item : cart.getItems()) {
            if (!item.getFoodItem().isAvailable()) {
                throw new InvalidOrderException(item.getFoodItem().getName() + " is unavailable");
            }
            subtotal = subtotal.add(item.getFoodItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        Coupon coupon = null;
        BigDecimal discount = BigDecimal.ZERO;
        if (StringUtils.hasText(request.couponCode())) {
            coupon = cartService.validateCoupon(request.couponCode(), subtotal);
            discount = cartService.calculateDiscount(coupon, subtotal);
        }

        BigDecimal deliveryFee = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal taxable = subtotal.subtract(discount);
        BigDecimal tax = taxable.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxable.add(deliveryFee).add(tax).setScale(2, RoundingMode.HALF_UP);

        CustomerOrder order = CustomerOrder.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .restaurant(cart.getRestaurant())
                .deliveryAddress(address)
                .coupon(coupon)
                .status(OrderStatus.PLACED)
                .subtotal(subtotal)
                .discountAmount(discount)
                .deliveryFee(deliveryFee)
                .taxAmount(tax)
                .totalAmount(total)
                .paymentMethod(request.paymentMethod())
                .build();

        cart.getItems().forEach(cartItem -> {
            BigDecimal itemTotal = cartItem.getFoodItem().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .foodItem(cartItem.getFoodItem())
                    .itemNameSnapshot(cartItem.getFoodItem().getName())
                    .unitPrice(cartItem.getFoodItem().getPrice())
                    .quantity(cartItem.getQuantity())
                    .itemTotal(itemTotal)
                    .build());
        });

        CustomerOrder savedOrder = orderRepository.save(order);
        paymentRepository.save(Payment.builder()
                .order(savedOrder)
                .paymentMethod(request.paymentMethod())
                .paymentStatus(request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY ? PaymentStatus.PENDING : PaymentStatus.SUCCESS)
                .amount(total)
                .transactionReference("MOCK-" + UUID.randomUUID())
                .paidAt(request.paymentMethod() == PaymentMethod.CASH_ON_DELIVERY ? null : Instant.now())
                .build());
        recordHistory(savedOrder, OrderStatus.PLACED, customer, "Order placed");

        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);

        return OrderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listOrders(OrderStatus status, Pageable pageable) {
        User user = currentUserService.getCurrentUser();
        if (currentUserService.hasRole(user, RoleName.ADMIN)) {
            return PageResponse.from((status == null ? orderRepository.findAll(pageable) : orderRepository.findByStatus(status, pageable))
                    .map(OrderMapper::toResponse));
        }
        if (currentUserService.hasRole(user, RoleName.RESTAURANT_OWNER)) {
            return PageResponse.from(orderRepository.findByRestaurant_Owner_Id(user.getId(), pageable)
                    .map(OrderMapper::toResponse));
        }
        return PageResponse.from(orderRepository.findByCustomerId(user.getId(), pageable)
                .map(OrderMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        CustomerOrder order = findAccessibleOrder(id);
        return OrderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        User user = currentUserService.getCurrentUser();
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        boolean ownsOrder = order.getCustomer().getId().equals(user.getId());
        if (!ownsOrder && !currentUserService.hasRole(user, RoleName.ADMIN)) {
            throw new UnauthorizedException("You cannot cancel this order");
        }
        if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new InvalidOrderException("Order cannot be cancelled at this stage");
        }
        order.setStatus(OrderStatus.CANCELLED);
        recordHistory(order, OrderStatus.CANCELLED, user, "Order cancelled");
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UpdateOrderStatusRequest request) {
        User user = currentUserService.getCurrentUser();
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        validateStatusUpdatePermission(order, user, request.status());
        validateTransition(order.getStatus(), request.status());

        order.setStatus(request.status());
        CustomerOrder saved = orderRepository.save(order);
        if (request.status() == OrderStatus.READY_FOR_PICKUP) {
            createDeliveryIfMissing(saved);
        }
        recordHistory(saved, request.status(), user, request.note());
        return OrderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> statusHistory(Long orderId) {
        findAccessibleOrder(orderId);
        return historyRepository.findByOrderIdOrderByChangedAtAsc(orderId)
                .stream()
                .map(OrderMapper::toHistoryResponse)
                .toList();
    }

    public CustomerOrder findAccessibleOrder(Long id) {
        User user = currentUserService.getCurrentUser();
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!canAccess(order, user)) {
            throw new UnauthorizedException("You cannot access this order");
        }
        return order;
    }

    public boolean canAccess(CustomerOrder order, User user) {
        if (currentUserService.hasRole(user, RoleName.ADMIN)) {
            return true;
        }
        if (order.getCustomer().getId().equals(user.getId())) {
            return true;
        }
        if (order.getRestaurant().getOwner().getId().equals(user.getId())) {
            return true;
        }
        return deliveryRepository.findByOrderId(order.getId())
                .map(delivery -> delivery.getDeliveryPartner() != null
                        && delivery.getDeliveryPartner().getUser().getId().equals(user.getId()))
                .orElse(false);
    }

    public void recordHistory(CustomerOrder order, OrderStatus status, User changedBy, String note) {
        historyRepository.save(OrderStatusHistory.builder()
                .order(order)
                .status(status)
                .changedBy(changedBy)
                .note(note)
                .build());
    }

    private void validateStatusUpdatePermission(CustomerOrder order, User user, OrderStatus target) {
        if (currentUserService.hasRole(user, RoleName.ADMIN)) {
            return;
        }
        if (order.getRestaurant().getOwner().getId().equals(user.getId())
                && List.of(OrderStatus.CONFIRMED, OrderStatus.REJECTED, OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP).contains(target)) {
            return;
        }
        if (currentUserService.hasRole(user, RoleName.DELIVERY_PARTNER)
                && List.of(OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED).contains(target)) {
            boolean assigned = deliveryRepository.findByOrderId(order.getId())
                    .map(delivery -> delivery.getDeliveryPartner() != null
                            && delivery.getDeliveryPartner().getUser().getId().equals(user.getId()))
                    .orElse(false);
            if (assigned) {
                return;
            }
        }
        throw new UnauthorizedException("You cannot move this order to the requested status");
    }

    private void validateTransition(OrderStatus current, OrderStatus target) {
        boolean valid = switch (current) {
            case PLACED -> target == OrderStatus.CONFIRMED || target == OrderStatus.REJECTED || target == OrderStatus.CANCELLED;
            case CONFIRMED -> target == OrderStatus.PREPARING || target == OrderStatus.CANCELLED;
            case PREPARING -> target == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> target == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> target == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED, REJECTED -> false;
        };
        if (!valid) {
            throw new InvalidOrderStatusTransitionException("Invalid order status transition from " + current + " to " + target);
        }
    }

    private void createDeliveryIfMissing(CustomerOrder order) {
        deliveryRepository.findByOrderId(order.getId()).orElseGet(() -> deliveryRepository.save(Delivery.builder()
                .order(order)
                .status(DeliveryStatus.ASSIGNED)
                .deliveryFee(order.getDeliveryFee())
                .assignedAt(Instant.now())
                .build()));
    }

    private String generateOrderNumber() {
        return "FH" + Instant.now().toEpochMilli() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

