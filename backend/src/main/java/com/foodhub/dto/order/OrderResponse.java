package com.foodhub.dto.order;

import com.foodhub.enums.OrderStatus;
import com.foodhub.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        Long restaurantId,
        String restaurantName,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal deliveryFee,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        Instant placedAt,
        List<OrderItemResponse> items
) {
}

