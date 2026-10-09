package com.foodhub.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long foodItemId,
        String foodName,
        String imageUrl,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal itemTotal
) {
}

