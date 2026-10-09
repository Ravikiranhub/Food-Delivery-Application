package com.foodhub.dto.order;

import com.foodhub.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "Delivery address is required")
        Long addressId,

        String couponCode,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}

