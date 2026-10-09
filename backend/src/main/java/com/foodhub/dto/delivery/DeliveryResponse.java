package com.foodhub.dto.delivery;

import com.foodhub.enums.DeliveryStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record DeliveryResponse(
        Long id,
        Long orderId,
        String orderNumber,
        String restaurantName,
        String customerName,
        DeliveryStatus status,
        BigDecimal deliveryFee,
        Instant assignedAt,
        Instant acceptedAt,
        Instant pickedUpAt,
        Instant deliveredAt
) {
}

