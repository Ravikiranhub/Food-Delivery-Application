package com.foodhub.mapper;

import com.foodhub.dto.food.FoodCategoryResponse;
import com.foodhub.dto.food.FoodItemResponse;
import com.foodhub.entity.FoodCategory;
import com.foodhub.entity.FoodItem;

public final class FoodMapper {

    private FoodMapper() {
    }

    public static FoodCategoryResponse toCategoryResponse(FoodCategory category) {
        return new FoodCategoryResponse(
                category.getId(),
                category.getRestaurant().getId(),
                category.getName(),
                category.isActive()
        );
    }

    public static FoodItemResponse toFoodItemResponse(FoodItem foodItem) {
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getRestaurant().getId(),
                foodItem.getRestaurant().getName(),
                foodItem.getCategory().getId(),
                foodItem.getCategory().getName(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getPrice(),
                foodItem.isVegetarian(),
                foodItem.isAvailable(),
                foodItem.getImageUrl(),
                foodItem.getPreparationTimeMinutes(),
                foodItem.getAverageRating()
        );
    }
}

