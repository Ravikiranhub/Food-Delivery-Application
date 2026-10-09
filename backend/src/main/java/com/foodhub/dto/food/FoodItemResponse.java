package com.foodhub.dto.food;

import java.math.BigDecimal;

public record FoodItemResponse(
        Long id,
        Long restaurantId,
        String restaurantName,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        BigDecimal price,
        boolean vegetarian,
        boolean available,
        String imageUrl,
        Integer preparationTimeMinutes,
        BigDecimal averageRating
) {
}

