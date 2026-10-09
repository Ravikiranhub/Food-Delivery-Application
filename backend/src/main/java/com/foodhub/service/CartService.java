package com.foodhub.service;

import com.foodhub.dto.cart.AddCartItemRequest;
import com.foodhub.dto.cart.ApplyCouponRequest;
import com.foodhub.dto.cart.CartResponse;
import com.foodhub.dto.cart.UpdateCartItemRequest;
import com.foodhub.entity.Cart;
import com.foodhub.entity.CartItem;
import com.foodhub.entity.Coupon;
import com.foodhub.entity.FoodItem;
import com.foodhub.entity.User;
import com.foodhub.enums.DiscountType;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.exception.CouponNotApplicableException;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.RestaurantNotAvailableException;
import com.foodhub.mapper.CartMapper;
import com.foodhub.repository.CartItemRepository;
import com.foodhub.repository.CartRepository;
import com.foodhub.repository.CouponRepository;
import com.foodhub.repository.FoodItemRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final CouponRepository couponRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public CartResponse getCart() {
        return CartMapper.toResponse(getOrCreateCart(currentUserService.getCurrentUser()));
    }

    @Transactional
    public CartResponse addItem(AddCartItemRequest request) {
        User user = currentUserService.getCurrentUser();
        Cart cart = getOrCreateCart(user);
        FoodItem foodItem = foodItemRepository.findById(request.foodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        if (!foodItem.isAvailable()) {
            throw new InvalidOrderException("Food item is currently unavailable");
        }
        if (foodItem.getRestaurant().getStatus() != RestaurantStatus.APPROVED || !foodItem.getRestaurant().isOpen()) {
            throw new RestaurantNotAvailableException("Restaurant is not accepting orders right now");
        }
        if (cart.getRestaurant() != null
                && !cart.getRestaurant().getId().equals(foodItem.getRestaurant().getId())
                && !cart.getItems().isEmpty()) {
            throw new InvalidOrderException("Cart already contains items from another restaurant");
        }

        cart.setRestaurant(foodItem.getRestaurant());
        CartItem item = cartItemRepository.findByCartIdAndFoodItemId(cart.getId(), foodItem.getId())
                .orElse(null);
        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(request.quantity())
                    .unitPriceSnapshot(foodItem.getPrice())
                    .build();
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + request.quantity());
        }
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(Long cartItemId, UpdateCartItemRequest request) {
        User user = currentUserService.getCurrentUser();
        CartItem item = cartItemRepository.findByIdAndCart_Customer_Id(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(request.quantity());
        cartItemRepository.save(item);
        return CartMapper.toResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(Long cartItemId) {
        User user = currentUserService.getCurrentUser();
        CartItem item = cartItemRepository.findByIdAndCart_Customer_Id(cartItemId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        if (cart.getItems().isEmpty()) {
            cart.setRestaurant(null);
        }
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse clearCart() {
        Cart cart = getOrCreateCart(currentUserService.getCurrentUser());
        cart.getItems().clear();
        cart.setRestaurant(null);
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Transactional
    public Map<String, Object> applyCoupon(ApplyCouponRequest request) {
        Cart cart = getOrCreateCart(currentUserService.getCurrentUser());
        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getFoodItem().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        Coupon coupon = validateCoupon(request.code(), subtotal);
        BigDecimal discount = calculateDiscount(coupon, subtotal);
        return Map.of(
                "code", coupon.getCode(),
                "subtotal", subtotal,
                "discountAmount", discount,
                "payableAfterDiscount", subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP)
        );
    }

    public Coupon validateCoupon(String code, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new CouponNotApplicableException("Invalid coupon code"));
        Instant now = Instant.now();
        if (!coupon.isActive() || now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
            throw new CouponNotApplicableException("Coupon is not active");
        }
        if (subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new CouponNotApplicableException("Cart total is below coupon minimum order amount");
        }
        return coupon;
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        BigDecimal discount = coupon.getDiscountType() == DiscountType.PERCENTAGE
                ? subtotal.multiply(coupon.getDiscountValue()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                : coupon.getDiscountValue();
        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(user).build()));
    }
}
