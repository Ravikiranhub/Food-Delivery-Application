package com.foodhub.dto.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "Order is required")
        Long orderId
) {
}

