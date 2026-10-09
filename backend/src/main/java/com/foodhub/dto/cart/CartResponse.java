package com.foodhub.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long restaurantId,
        String restaurantName,
        List<CartItemResponse> items,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal taxAmount,
        BigDecimal totalAmount
) {
}

