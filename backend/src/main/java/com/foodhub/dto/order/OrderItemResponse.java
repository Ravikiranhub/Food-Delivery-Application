package com.foodhub.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long foodItemId,
        String name,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal itemTotal
) {
}

