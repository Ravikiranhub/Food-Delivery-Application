package com.foodhub.repository;

import com.foodhub.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            select oi.itemNameSnapshot, sum(oi.quantity)
            from OrderItem oi
            where oi.order.restaurant.owner.id = :ownerId
            group by oi.itemNameSnapshot
            order by sum(oi.quantity) desc
            """)
    List<Object[]> popularFoodsByOwner(@Param("ownerId") Long ownerId);
}

