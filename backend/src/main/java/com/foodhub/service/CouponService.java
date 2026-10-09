package com.foodhub.service;

import com.foodhub.dto.admin.CouponRequest;
import com.foodhub.dto.admin.CouponResponse;
import com.foodhub.entity.Coupon;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.mapper.CouponMapper;
import com.foodhub.repository.CouponRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<CouponResponse> list() {
        return couponRepository.findAll().stream().map(CouponMapper::toResponse).toList();
    }

    @Transactional
    public CouponResponse create(CouponRequest request) {
        Coupon coupon = Coupon.builder()
                .code(request.code().toUpperCase())
                .discountType(request.discountType())
                .discountValue(request.discountValue())
                .minOrderAmount(request.minOrderAmount())
                .maxDiscountAmount(request.maxDiscountAmount())
                .validFrom(request.validFrom())
                .validUntil(request.validUntil())
                .active(request.active())
                .build();
        return CouponMapper.toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setCode(request.code().toUpperCase());
        coupon.setDiscountType(request.discountType());
        coupon.setDiscountValue(request.discountValue());
        coupon.setMinOrderAmount(request.minOrderAmount());
        coupon.setMaxDiscountAmount(request.maxDiscountAmount());
        coupon.setValidFrom(request.validFrom());
        coupon.setValidUntil(request.validUntil());
        coupon.setActive(request.active());
        return CouponMapper.toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public CouponResponse setActive(Long id, boolean active) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setActive(active);
        return CouponMapper.toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public void delete(Long id) {
        couponRepository.deleteById(id);
    }
}

