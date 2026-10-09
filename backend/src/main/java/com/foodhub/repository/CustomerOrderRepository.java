package com.foodhub.repository;

import com.foodhub.entity.CustomerOrder;
import com.foodhub.enums.OrderStatus;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Page<CustomerOrder> findByCustomerId(Long customerId, Pageable pageable);

    Page<CustomerOrder> findByRestaurant_Owner_Id(Long ownerId, Pageable pageable);

    Page<CustomerOrder> findByStatus(OrderStatus status, Pageable pageable);

    long countByCustomerId(Long customerId);

    long countByRestaurant_Owner_Id(Long ownerId);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from CustomerOrder o where o.status = :status")
    BigDecimal sumTotalAmountByStatus(@Param("status") OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from CustomerOrder o where o.restaurant.owner.id = :ownerId and o.status = :status")
    BigDecimal sumOwnerRevenue(@Param("ownerId") Long ownerId, @Param("status") OrderStatus status);
}
