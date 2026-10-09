package com.foodhub.dto.payment;

import com.foodhub.enums.PaymentMethod;
import com.foodhub.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long orderId,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String transactionReference,
        Instant paidAt
) {
}

