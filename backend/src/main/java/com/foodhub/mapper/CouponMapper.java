package com.foodhub.mapper;

import com.foodhub.dto.admin.CouponResponse;
import com.foodhub.entity.Coupon;

public final class CouponMapper {

    private CouponMapper() {
    }

    public static CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderAmount(),
                coupon.getMaxDiscountAmount(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.isActive()
        );
    }
}

