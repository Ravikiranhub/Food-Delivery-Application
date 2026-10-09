package com.foodhub.dto.restaurant;

import com.foodhub.enums.RestaurantStatus;
import java.math.BigDecimal;

public record RestaurantResponse(
        Long id,
        Long ownerId,
        String ownerName,
        String name,
        String description,
        String cuisineType,
        String phone,
        String email,
        String imageUrl,
        RestaurantStatus status,
        boolean open,
        BigDecimal averageRating,
        String city
) {
}

