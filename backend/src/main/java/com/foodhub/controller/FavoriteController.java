package com.foodhub.controller;

import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public List<RestaurantResponse> favorites() {
        return favoriteService.favorites();
    }

    @PostMapping("/restaurants/{restaurantId}")
    public RestaurantResponse add(@PathVariable Long restaurantId) {
        return favoriteService.add(restaurantId);
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public void remove(@PathVariable Long restaurantId) {
        favoriteService.remove(restaurantId);
    }
}

