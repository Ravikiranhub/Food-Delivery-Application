package com.foodhub.repository;

import com.foodhub.entity.Delivery;
import com.foodhub.enums.DeliveryStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByDeliveryPartnerIdOrderByAssignedAtDesc(Long partnerId);

    List<Delivery> findByDeliveryPartnerIdAndStatusIn(Long partnerId, List<DeliveryStatus> statuses);

    List<Delivery> findByStatusAndDeliveryPartnerIsNull(DeliveryStatus status);

    long countByDeliveryPartnerId(Long partnerId);

    long countByDeliveryPartnerIdAndStatus(Long partnerId, DeliveryStatus status);
}
