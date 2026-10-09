package com.foodhub.dto.cart;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequest(
        @NotBlank(message = "Coupon code is required")
        String code
) {
}

