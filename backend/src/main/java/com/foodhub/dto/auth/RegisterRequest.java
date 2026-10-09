package com.foodhub.dto.auth;

import com.foodhub.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 120, message = "Name must be between 2 and 120 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 80, message = "Password must be between 8 and 80 characters")
        String password,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must contain 10 to 15 digits")
        String phone,

        RoleName role,

        String vehicleType,

        String vehicleNumber
) {
}

