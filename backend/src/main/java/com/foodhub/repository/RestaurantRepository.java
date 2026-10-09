package com.foodhub.repository;

import com.foodhub.entity.Restaurant;
import com.foodhub.enums.RestaurantStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("""
            select r from Restaurant r
            where (:status is null or r.status = :status)
              and (:keyword is null or lower(r.name) like lower(concat('%', :keyword, '%'))
                   or lower(r.description) like lower(concat('%', :keyword, '%')))
              and (:cuisine is null or lower(r.cuisineType) = lower(:cuisine))
              and (:city is null or lower(r.city) = lower(:city))
              and (:open is null or r.open = :open)
            """)
    Page<Restaurant> search(
            @Param("keyword") String keyword,
            @Param("cuisine") String cuisine,
            @Param("city") String city,
            @Param("open") Boolean open,
            @Param("status") RestaurantStatus status,
            Pageable pageable
    );

    List<Restaurant> findByOwnerId(Long ownerId);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    long countByStatus(RestaurantStatus status);
}

