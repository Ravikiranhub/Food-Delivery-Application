package com.foodhub.dto.food;

public record FoodCategoryResponse(
        Long id,
        Long restaurantId,
        String name,
        boolean active
) {
}

