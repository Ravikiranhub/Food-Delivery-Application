package com.foodhub.repository;

import com.foodhub.entity.Favorite;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Optional<Favorite> findByCustomerIdAndRestaurantId(Long customerId, Long restaurantId);

    boolean existsByCustomerIdAndRestaurantId(Long customerId, Long restaurantId);
}

