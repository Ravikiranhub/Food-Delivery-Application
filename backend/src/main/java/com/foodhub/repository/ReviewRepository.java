package com.foodhub.repository;

import com.foodhub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

    Page<Review> findByFoodItemId(Long foodItemId, Pageable pageable);

    boolean existsByCustomer_IdAndOrder_IdAndRestaurant_Id(Long customerId, Long orderId, Long restaurantId);

    @Query("select avg(r.rating) from Review r where r.restaurant.id = :restaurantId")
    Double averageRestaurantRating(@Param("restaurantId") Long restaurantId);

    @Query("select avg(r.rating) from Review r where r.foodItem.id = :foodItemId")
    Double averageFoodRating(@Param("foodItemId") Long foodItemId);
}
