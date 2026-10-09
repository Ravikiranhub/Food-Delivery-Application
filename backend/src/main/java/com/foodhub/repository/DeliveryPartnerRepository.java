package com.foodhub.repository;

import com.foodhub.entity.DeliveryPartner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {
    Optional<DeliveryPartner> findByUserId(Long userId);

    long countByAvailableTrue();
}

