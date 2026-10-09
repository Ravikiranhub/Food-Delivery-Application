package com.foodhub.service;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.review.ReviewRequest;
import com.foodhub.dto.review.ReviewResponse;
import com.foodhub.entity.CustomerOrder;
import com.foodhub.entity.FoodItem;
import com.foodhub.entity.Restaurant;
import com.foodhub.entity.Review;
import com.foodhub.entity.User;
import com.foodhub.enums.OrderStatus;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.ReviewMapper;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.FoodItemRepository;
import com.foodhub.repository.RestaurantRepository;
import com.foodhub.repository.ReviewRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        User customer = currentUserService.getCurrentUser();
        CustomerOrder order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new UnauthorizedException("You can review only your own orders");
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Reviews are allowed only after delivery");
        }
        if (!order.getRestaurant().getId().equals(request.restaurantId())) {
            throw new InvalidOrderException("Restaurant does not match the order");
        }
        if (reviewRepository.existsByCustomer_IdAndOrder_IdAndRestaurant_Id(customer.getId(), request.orderId(), request.restaurantId())) {
            throw new InvalidOrderException("You already reviewed this order");
        }

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        FoodItem foodItem = null;
        if (request.foodItemId() != null) {
            foodItem = foodItemRepository.findById(request.foodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
            if (!foodItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new InvalidOrderException("Food item does not belong to the restaurant");
            }
        }

        Review saved = reviewRepository.save(Review.builder()
                .customer(customer)
                .restaurant(restaurant)
                .foodItem(foodItem)
                .order(order)
                .rating(request.rating())
                .comment(request.comment())
                .build());
        refreshRatings(restaurant, foodItem);
        return ReviewMapper.toResponse(saved);
    }

    public PageResponse<ReviewResponse> restaurantReviews(Long restaurantId, Pageable pageable) {
        return PageResponse.from(reviewRepository.findByRestaurantId(restaurantId, pageable)
                .map(ReviewMapper::toResponse));
    }

    public PageResponse<ReviewResponse> foodReviews(Long foodItemId, Pageable pageable) {
        return PageResponse.from(reviewRepository.findByFoodItemId(foodItemId, pageable)
                .map(ReviewMapper::toResponse));
    }

    @Transactional
    public void delete(Long id) {
        User user = currentUserService.getCurrentUser();
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (!review.getCustomer().getId().equals(user.getId()) && !currentUserService.hasRole(user, com.foodhub.enums.RoleName.ADMIN)) {
            throw new UnauthorizedException("You can delete only your own review");
        }
        Restaurant restaurant = review.getRestaurant();
        FoodItem foodItem = review.getFoodItem();
        reviewRepository.delete(review);
        refreshRatings(restaurant, foodItem);
    }

    private void refreshRatings(Restaurant restaurant, FoodItem foodItem) {
        Double restaurantAverage = reviewRepository.averageRestaurantRating(restaurant.getId());
        restaurant.setAverageRating(toRating(restaurantAverage));
        restaurantRepository.save(restaurant);
        if (foodItem != null) {
            Double foodAverage = reviewRepository.averageFoodRating(foodItem.getId());
            foodItem.setAverageRating(toRating(foodAverage));
            foodItemRepository.save(foodItem);
        }
    }

    private BigDecimal toRating(Double average) {
        if (average == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP);
    }
}
