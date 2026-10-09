package com.foodhub.dto.admin;

import com.foodhub.enums.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Instant;

public record CouponRequest(
        @NotBlank(message = "Coupon code is required")
        String code,

        @NotNull(message = "Discount type is required")
        DiscountType discountType,

        @NotNull(message = "Discount value is required")
        @Positive(message = "Discount value must be positive")
        BigDecimal discountValue,

        @NotNull(message = "Minimum order amount is required")
        @PositiveOrZero(message = "Minimum order amount cannot be negative")
        BigDecimal minOrderAmount,

        BigDecimal maxDiscountAmount,

        @NotNull(message = "Valid from is required")
        Instant validFrom,

        @NotNull(message = "Valid until is required")
        @Future(message = "Valid until must be in the future")
        Instant validUntil,

        boolean active
) {
}

