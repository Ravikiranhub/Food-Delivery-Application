package com.foodhub.repository;

import com.foodhub.entity.FoodItem;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query("""
            select f from FoodItem f
            where (:restaurantId is null or f.restaurant.id = :restaurantId)
              and (:categoryId is null or f.category.id = :categoryId)
              and (:keyword is null or lower(f.name) like lower(concat('%', :keyword, '%'))
                   or lower(f.description) like lower(concat('%', :keyword, '%')))
              and (:vegetarian is null or f.vegetarian = :vegetarian)
              and (:available is null or f.available = :available)
            """)
    Page<FoodItem> search(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("vegetarian") Boolean vegetarian,
            @Param("available") Boolean available,
            Pageable pageable
    );

    List<FoodItem> findTop8ByAvailableTrueOrderByAverageRatingDescIdDesc();
}

