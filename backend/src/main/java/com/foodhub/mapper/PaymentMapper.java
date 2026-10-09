package com.foodhub.mapper;

import com.foodhub.dto.payment.PaymentResponse;
import com.foodhub.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getAmount(),
                payment.getTransactionReference(),
                payment.getPaidAt()
        );
    }
}

