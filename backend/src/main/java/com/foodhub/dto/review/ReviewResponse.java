package com.foodhub.dto.review;

import java.time.Instant;

public record ReviewResponse(
        Long id,
        Long customerId,
        String customerName,
        Long restaurantId,
        Long foodItemId,
        Integer rating,
        String comment,
        Instant createdAt
) {
}

