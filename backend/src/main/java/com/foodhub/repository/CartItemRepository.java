package com.foodhub.repository;

import com.foodhub.entity.CartItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByIdAndCart_Customer_Id(Long id, Long customerId);

    Optional<CartItem> findByCartIdAndFoodItemId(Long cartId, Long foodItemId);
}
