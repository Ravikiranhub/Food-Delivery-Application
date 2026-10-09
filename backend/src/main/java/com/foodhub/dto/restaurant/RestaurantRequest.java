package com.foodhub.dto.restaurant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RestaurantRequest(
        @NotBlank(message = "Restaurant name is required")
        @Size(max = 140, message = "Restaurant name cannot exceed 140 characters")
        String name,

        @Size(max = 800, message = "Description cannot exceed 800 characters")
        String description,

        @NotBlank(message = "Cuisine type is required")
        String cuisineType,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must contain 10 to 15 digits")
        String phone,

        @Email(message = "Email must be valid")
        String email,

        String imageUrl,

        @NotBlank(message = "City is required")
        String city
) {
}

