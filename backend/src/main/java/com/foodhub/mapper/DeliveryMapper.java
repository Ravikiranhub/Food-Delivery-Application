package com.foodhub.mapper;

import com.foodhub.dto.delivery.DeliveryResponse;
import com.foodhub.entity.Delivery;

public final class DeliveryMapper {

    private DeliveryMapper() {
    }

    public static DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrder().getId(),
                delivery.getOrder().getOrderNumber(),
                delivery.getOrder().getRestaurant().getName(),
                delivery.getOrder().getCustomer().getName(),
                delivery.getStatus(),
                delivery.getDeliveryFee(),
                delivery.getAssignedAt(),
                delivery.getAcceptedAt(),
                delivery.getPickedUpAt(),
                delivery.getDeliveredAt()
        );
    }
}

