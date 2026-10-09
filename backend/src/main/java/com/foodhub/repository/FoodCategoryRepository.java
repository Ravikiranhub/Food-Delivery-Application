package com.foodhub.repository;

import com.foodhub.entity.FoodCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {
    List<FoodCategory> findByRestaurantIdAndActiveTrueOrderByNameAsc(Long restaurantId);

    Optional<FoodCategory> findByIdAndRestaurantId(Long id, Long restaurantId);
}

