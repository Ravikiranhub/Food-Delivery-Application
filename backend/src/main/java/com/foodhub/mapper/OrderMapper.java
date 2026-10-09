package com.foodhub.mapper;

import com.foodhub.dto.order.OrderItemResponse;
import com.foodhub.dto.order.OrderResponse;
import com.foodhub.dto.order.OrderStatusHistoryResponse;
import com.foodhub.entity.CustomerOrder;
import com.foodhub.entity.OrderItem;
import com.foodhub.entity.OrderStatusHistory;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getRestaurant().getId(),
                order.getRestaurant().getName(),
                order.getStatus(),
                order.getSubtotal(),
                order.getDiscountAmount(),
                order.getDeliveryFee(),
                order.getTaxAmount(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getPlacedAt(),
                order.getItems().stream().map(OrderMapper::toItemResponse).toList()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getFoodItem().getId(),
                item.getItemNameSnapshot(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getItemTotal()
        );
    }

    public static OrderStatusHistoryResponse toHistoryResponse(OrderStatusHistory history) {
        return new OrderStatusHistoryResponse(
                history.getId(),
                history.getStatus(),
                history.getChangedBy() == null ? "SYSTEM" : history.getChangedBy().getName(),
                history.getNote(),
                history.getChangedAt()
        );
    }
}

