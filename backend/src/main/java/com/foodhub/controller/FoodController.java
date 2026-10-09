package com.foodhub.controller;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.food.FoodCategoryRequest;
import com.foodhub.dto.food.FoodCategoryResponse;
import com.foodhub.dto.food.FoodItemRequest;
import com.foodhub.dto.food.FoodItemResponse;
import com.foodhub.service.FoodService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @GetMapping("/api/restaurants/{restaurantId}/categories")
    public List<FoodCategoryResponse> categories(@PathVariable Long restaurantId) {
        return foodService.categories(restaurantId);
    }

    @PostMapping("/api/restaurants/{restaurantId}/categories")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodCategoryResponse createCategory(@PathVariable Long restaurantId, @Valid @RequestBody FoodCategoryRequest request) {
        return foodService.createCategory(restaurantId, request);
    }

    @PutMapping("/api/categories/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodCategoryResponse updateCategory(@PathVariable Long id, @Valid @RequestBody FoodCategoryRequest request) {
        return foodService.updateCategory(id, request);
    }

    @DeleteMapping("/api/categories/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public void deleteCategory(@PathVariable Long id) {
        foodService.deleteCategory(id);
    }

    @GetMapping("/api/food-items")
    public PageResponse<FoodItemResponse> listFood(
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Boolean available,
            Pageable pageable
    ) {
        return foodService.list(restaurantId, categoryId, keyword, vegetarian, available, pageable);
    }

    @GetMapping("/api/food-items/{id}")
    public FoodItemResponse getFood(@PathVariable Long id) {
        return foodService.get(id);
    }

    @GetMapping("/api/restaurants/{restaurantId}/food-items")
    public PageResponse<FoodItemResponse> restaurantFood(@PathVariable Long restaurantId, Pageable pageable) {
        return foodService.list(restaurantId, null, null, null, true, pageable);
    }

    @PostMapping("/api/restaurants/{restaurantId}/food-items")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodItemResponse createFood(@PathVariable Long restaurantId, @Valid @RequestBody FoodItemRequest request) {
        return foodService.createFood(restaurantId, request);
    }

    @PutMapping("/api/food-items/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodItemResponse updateFood(@PathVariable Long id, @Valid @RequestBody FoodItemRequest request) {
        return foodService.updateFood(id, request);
    }

    @DeleteMapping("/api/food-items/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public void deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
    }

    @PatchMapping("/api/food-items/{id}/availability")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodItemResponse availability(@PathVariable Long id, @RequestParam boolean available) {
        return foodService.setAvailability(id, available);
    }

    @PatchMapping("/api/food-items/{id}/price")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public FoodItemResponse price(@PathVariable Long id, @RequestParam BigDecimal price) {
        return foodService.updatePrice(id, price);
    }
}

