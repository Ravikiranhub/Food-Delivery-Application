package com.foodhub.mapper;

import com.foodhub.dto.cart.CartItemResponse;
import com.foodhub.dto.cart.CartResponse;
import com.foodhub.entity.Cart;
import com.foodhub.entity.CartItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class CartMapper {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.05");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("30.00");

    private CartMapper() {
    }

    public static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartMapper::toItemResponse)
                .toList();
        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::itemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal deliveryFee = subtotal.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? BigDecimal.ZERO : DELIVERY_FEE;
        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(deliveryFee).add(tax).setScale(2, RoundingMode.HALF_UP);

        return new CartResponse(
                cart.getId(),
                cart.getRestaurant() == null ? null : cart.getRestaurant().getId(),
                cart.getRestaurant() == null ? null : cart.getRestaurant().getName(),
                items,
                subtotal,
                deliveryFee,
                tax,
                total
        );
    }

    private static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal itemTotal = item.getFoodItem().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
        return new CartItemResponse(
                item.getId(),
                item.getFoodItem().getId(),
                item.getFoodItem().getName(),
                item.getFoodItem().getImageUrl(),
                item.getFoodItem().getPrice(),
                item.getQuantity(),
                itemTotal
        );
    }
}

