package com.foodhub.service;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.food.FoodCategoryRequest;
import com.foodhub.dto.food.FoodCategoryResponse;
import com.foodhub.dto.food.FoodItemRequest;
import com.foodhub.dto.food.FoodItemResponse;
import com.foodhub.entity.FoodCategory;
import com.foodhub.entity.FoodItem;
import com.foodhub.entity.Restaurant;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.FoodMapper;
import com.foodhub.repository.FoodCategoryRepository;
import com.foodhub.repository.FoodItemRepository;
import com.foodhub.repository.RestaurantRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodCategoryRepository categoryRepository;
    private final FoodItemRepository foodItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<FoodCategoryResponse> categories(Long restaurantId) {
        return categoryRepository.findByRestaurantIdAndActiveTrueOrderByNameAsc(restaurantId)
                .stream()
                .map(FoodMapper::toCategoryResponse)
                .toList();
    }

    @Transactional
    public FoodCategoryResponse createCategory(Long restaurantId, FoodCategoryRequest request) {
        Restaurant restaurant = ownedRestaurant(restaurantId);
        FoodCategory category = FoodCategory.builder()
                .restaurant(restaurant)
                .name(request.name())
                .active(true)
                .build();
        return FoodMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public FoodCategoryResponse updateCategory(Long id, FoodCategoryRequest request) {
        FoodCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        ensureOwner(category.getRestaurant());
        category.setName(request.name());
        return FoodMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        FoodCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        ensureOwner(category.getRestaurant());
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public PageResponse<FoodItemResponse> list(
            Long restaurantId,
            Long categoryId,
            String keyword,
            Boolean vegetarian,
            Boolean available,
            Pageable pageable
    ) {
        return PageResponse.from(foodItemRepository
                .search(restaurantId, categoryId, blankToNull(keyword), vegetarian, available, pageable)
                .map(FoodMapper::toFoodItemResponse));
    }

    @Transactional(readOnly = true)
    public FoodItemResponse get(Long id) {
        return FoodMapper.toFoodItemResponse(foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found")));
    }

    @Transactional
    public FoodItemResponse createFood(Long restaurantId, FoodItemRequest request) {
        Restaurant restaurant = ownedRestaurant(restaurantId);
        FoodCategory category = categoryRepository.findByIdAndRestaurantId(request.categoryId(), restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for restaurant"));
        FoodItem item = FoodItem.builder()
                .restaurant(restaurant)
                .category(category)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .vegetarian(request.vegetarian())
                .available(request.available())
                .imageUrl(request.imageUrl())
                .preparationTimeMinutes(request.preparationTimeMinutes())
                .averageRating(BigDecimal.ZERO)
                .build();
        return FoodMapper.toFoodItemResponse(foodItemRepository.save(item));
    }

    @Transactional
    public FoodItemResponse updateFood(Long id, FoodItemRequest request) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        ensureOwner(item.getRestaurant());
        FoodCategory category = categoryRepository.findByIdAndRestaurantId(request.categoryId(), item.getRestaurant().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found for restaurant"));
        item.setCategory(category);
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setVegetarian(request.vegetarian());
        item.setAvailable(request.available());
        item.setImageUrl(request.imageUrl());
        item.setPreparationTimeMinutes(request.preparationTimeMinutes());
        return FoodMapper.toFoodItemResponse(foodItemRepository.save(item));
    }

    @Transactional
    public void deleteFood(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        ensureOwner(item.getRestaurant());
        item.setAvailable(false);
        foodItemRepository.save(item);
    }

    @Transactional
    public FoodItemResponse setAvailability(Long id, boolean available) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        ensureOwner(item.getRestaurant());
        item.setAvailable(available);
        return FoodMapper.toFoodItemResponse(foodItemRepository.save(item));
    }

    @Transactional
    public FoodItemResponse updatePrice(Long id, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        ensureOwner(item.getRestaurant());
        item.setPrice(price);
        return FoodMapper.toFoodItemResponse(foodItemRepository.save(item));
    }

    private Restaurant ownedRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        ensureOwner(restaurant);
        return restaurant;
    }

    private void ensureOwner(Restaurant restaurant) {
        Long currentUserId = currentUserService.getCurrentUser().getId();
        if (!restaurant.getOwner().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can manage only your own restaurant menu");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
