package com.foodhub.dto.food;

import jakarta.validation.constraints.NotBlank;

public record FoodCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name
) {
}

