package com.foodhub.dto.admin;

import com.foodhub.enums.DiscountType;
import java.math.BigDecimal;
import java.time.Instant;

public record CouponResponse(
        Long id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderAmount,
        BigDecimal maxDiscountAmount,
        Instant validFrom,
        Instant validUntil,
        boolean active
) {
}

