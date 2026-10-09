package com.foodhub.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull(message = "Restaurant is required")
        Long restaurantId,

        Long foodItemId,

        @NotNull(message = "Order is required")
        Long orderId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        Integer rating,

        @Size(max = 800, message = "Comment cannot exceed 800 characters")
        String comment
) {
}

