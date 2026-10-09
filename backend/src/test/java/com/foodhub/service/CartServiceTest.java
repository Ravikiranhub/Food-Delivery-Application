package com.foodhub.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.foodhub.dto.cart.AddCartItemRequest;
import com.foodhub.entity.Cart;
import com.foodhub.entity.FoodCategory;
import com.foodhub.entity.FoodItem;
import com.foodhub.entity.Restaurant;
import com.foodhub.entity.User;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.repository.CartItemRepository;
import com.foodhub.repository.CartRepository;
import com.foodhub.repository.CouponRepository;
import com.foodhub.repository.FoodItemRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CartService cartService;

    @Test
    void addUnavailableFoodItemFails() {
        User user = User.builder().id(1L).email("customer@example.com").build();
        Restaurant restaurant = Restaurant.builder()
                .id(10L)
                .status(RestaurantStatus.APPROVED)
                .open(true)
                .name("Cafe")
                .build();
        FoodItem foodItem = FoodItem.builder()
                .id(20L)
                .restaurant(restaurant)
                .category(FoodCategory.builder().id(30L).name("Meals").restaurant(restaurant).build())
                .name("Paneer Bowl")
                .price(new BigDecimal("180.00"))
                .available(false)
                .build();

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(Cart.builder().id(40L).customer(user).build()));
        when(foodItemRepository.findById(20L)).thenReturn(Optional.of(foodItem));

        assertThatThrownBy(() -> cartService.addItem(new AddCartItemRequest(20L, 1)))
                .isInstanceOf(InvalidOrderException.class)
                .hasMessageContaining("unavailable");
    }
}

