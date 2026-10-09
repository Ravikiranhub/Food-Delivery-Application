package com.foodhub.service;

import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.entity.Favorite;
import com.foodhub.entity.Restaurant;
import com.foodhub.entity.User;
import com.foodhub.exception.InvalidOrderException;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.mapper.RestaurantMapper;
import com.foodhub.repository.FavoriteRepository;
import com.foodhub.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<RestaurantResponse> favorites() {
        User customer = currentUserService.getCurrentUser();
        return favoriteRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(favorite -> RestaurantMapper.toResponse(favorite.getRestaurant()))
                .toList();
    }

    @Transactional
    public RestaurantResponse add(Long restaurantId) {
        User customer = currentUserService.getCurrentUser();
        if (favoriteRepository.existsByCustomerIdAndRestaurantId(customer.getId(), restaurantId)) {
            throw new InvalidOrderException("Restaurant is already in favorites");
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        favoriteRepository.save(Favorite.builder().customer(customer).restaurant(restaurant).build());
        return RestaurantMapper.toResponse(restaurant);
    }

    @Transactional
    public void remove(Long restaurantId) {
        User customer = currentUserService.getCurrentUser();
        Favorite favorite = favoriteRepository.findByCustomerIdAndRestaurantId(customer.getId(), restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        favoriteRepository.delete(favorite);
    }
}
