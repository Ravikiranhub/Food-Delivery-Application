package com.foodhub.mapper;

import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.entity.Restaurant;

public final class RestaurantMapper {

    private RestaurantMapper() {
    }

    public static RestaurantResponse toResponse(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getOwner().getId(),
                restaurant.getOwner().getName(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCuisineType(),
                restaurant.getPhone(),
                restaurant.getEmail(),
                restaurant.getImageUrl(),
                restaurant.getStatus(),
                restaurant.isOpen(),
                restaurant.getAverageRating(),
                restaurant.getCity()
        );
    }
}

