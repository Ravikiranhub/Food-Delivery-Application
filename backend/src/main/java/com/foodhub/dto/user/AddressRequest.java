package com.foodhub.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressRequest(
        @NotBlank(message = "Label is required")
        String label,

        @NotBlank(message = "Address line 1 is required")
        String line1,

        String line2,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Pincode is required")
        @Pattern(regexp = "^[0-9]{5,10}$", message = "Pincode must contain 5 to 10 digits")
        String pincode,

        boolean defaultAddress
) {
}

