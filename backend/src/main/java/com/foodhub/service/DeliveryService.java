package com.foodhub.service;

import com.foodhub.dto.delivery.DeliveryResponse;
import com.foodhub.entity.CustomerOrder;
import com.foodhub.entity.Delivery;
import com.foodhub.entity.DeliveryPartner;
import com.foodhub.entity.User;
import com.foodhub.enums.DeliveryStatus;
import com.foodhub.enums.OrderStatus;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.DeliveryMapper;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.DeliveryPartnerRepository;
import com.foodhub.repository.DeliveryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPartnerRepository partnerRepository;
    private final CustomerOrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public List<DeliveryResponse> availableOrders() {
        return deliveryRepository.findByStatusAndDeliveryPartnerIsNull(DeliveryStatus.ASSIGNED)
                .stream()
                .map(DeliveryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> assignedOrders() {
        DeliveryPartner partner = currentPartner();
        return deliveryRepository.findByDeliveryPartnerIdAndStatusIn(
                        partner.getId(),
                        List.of(DeliveryStatus.ASSIGNED, DeliveryStatus.ACCEPTED, DeliveryStatus.PICKED_UP)
                )
                .stream()
                .map(DeliveryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> history() {
        DeliveryPartner partner = currentPartner();
        return deliveryRepository.findByDeliveryPartnerIdOrderByAssignedAtDesc(partner.getId())
                .stream()
                .map(DeliveryMapper::toResponse)
                .toList();
    }

    @Transactional
    public DeliveryResponse accept(Long orderId) {
        DeliveryPartner partner = currentPartner();
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new InvalidOrderException("Order is not ready for pickup");
        }
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseGet(() -> deliveryRepository.save(Delivery.builder()
                        .order(order)
                        .status(DeliveryStatus.ASSIGNED)
                        .deliveryFee(order.getDeliveryFee())
                        .assignedAt(Instant.now())
                        .build()));
        if (delivery.getDeliveryPartner() != null && !delivery.getDeliveryPartner().getId().equals(partner.getId())) {
            throw new UnauthorizedException("Delivery is already assigned to another partner");
        }
        delivery.setDeliveryPartner(partner);
        delivery.setStatus(DeliveryStatus.ACCEPTED);
        delivery.setAcceptedAt(Instant.now());
        return DeliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse pickedUp(Long orderId) {
        Delivery delivery = ownDelivery(orderId);
        if (delivery.getStatus() != DeliveryStatus.ACCEPTED) {
            throw new InvalidOrderException("Delivery must be accepted before pickup");
        }
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickedUpAt(Instant.now());
        delivery.getOrder().setStatus(OrderStatus.OUT_FOR_DELIVERY);
        orderRepository.save(delivery.getOrder());
        orderService.recordHistory(delivery.getOrder(), OrderStatus.OUT_FOR_DELIVERY, currentUserService.getCurrentUser(), "Picked up by delivery partner");
        return DeliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse delivered(Long orderId) {
        Delivery delivery = ownDelivery(orderId);
        if (delivery.getStatus() != DeliveryStatus.PICKED_UP) {
            throw new InvalidOrderException("Order must be picked up before delivery");
        }
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(Instant.now());
        delivery.getOrder().setStatus(OrderStatus.DELIVERED);
        orderRepository.save(delivery.getOrder());
        DeliveryPartner partner = delivery.getDeliveryPartner();
        partner.setTotalEarnings(partner.getTotalEarnings().add(delivery.getDeliveryFee()));
        partnerRepository.save(partner);
        orderService.recordHistory(delivery.getOrder(), OrderStatus.DELIVERED, currentUserService.getCurrentUser(), "Delivered to customer");
        return DeliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public Map<String, Object> setAvailability(boolean available) {
        DeliveryPartner partner = currentPartner();
        partner.setAvailable(available);
        partnerRepository.save(partner);
        return Map.of("available", available);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> earnings() {
        DeliveryPartner partner = currentPartner();
        return Map.of("totalEarnings", partner.getTotalEarnings(), "partnerId", partner.getId());
    }

    private Delivery ownDelivery(Long orderId) {
        DeliveryPartner partner = currentPartner();
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found"));
        if (delivery.getDeliveryPartner() == null || !delivery.getDeliveryPartner().getId().equals(partner.getId())) {
            throw new UnauthorizedException("Delivery is not assigned to you");
        }
        return delivery;
    }

    private DeliveryPartner currentPartner() {
        User user = currentUserService.getCurrentUser();
        return partnerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery partner profile not found"));
    }
}
