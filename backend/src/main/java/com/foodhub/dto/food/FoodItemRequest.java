package com.foodhub.dto.food;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record FoodItemRequest(
        @NotNull(message = "Category is required")
        Long categoryId,

        @NotBlank(message = "Food name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        boolean vegetarian,

        boolean available,

        String imageUrl,

        @NotNull(message = "Preparation time is required")
        @Min(value = 1, message = "Preparation time must be at least one minute")
        Integer preparationTimeMinutes
) {
}

