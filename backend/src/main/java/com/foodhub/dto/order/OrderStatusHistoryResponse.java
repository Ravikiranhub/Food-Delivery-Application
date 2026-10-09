package com.foodhub.dto.order;

import com.foodhub.enums.OrderStatus;
import java.time.Instant;

public record OrderStatusHistoryResponse(
        Long id,
        OrderStatus status,
        String changedBy,
        String note,
        Instant changedAt
) {
}

