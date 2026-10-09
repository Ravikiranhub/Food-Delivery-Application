package com.foodhub.service;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.restaurant.RestaurantRequest;
import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.entity.Restaurant;
import com.foodhub.entity.User;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.enums.RoleName;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.RestaurantMapper;
import com.foodhub.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CurrentUserService currentUserService;

    @Value("${app.restaurants.auto-approve:false}")
    private boolean autoApproveRestaurants;

    @Transactional(readOnly = true)
    public PageResponse<RestaurantResponse> listApproved(
            String keyword,
            String cuisine,
            String city,
            Boolean open,
            Pageable pageable
    ) {
        return PageResponse.from(restaurantRepository
                .search(blankToNull(keyword), blankToNull(cuisine), blankToNull(city), open, RestaurantStatus.APPROVED, pageable)
                .map(RestaurantMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getPublic(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        if (restaurant.getStatus() != RestaurantStatus.APPROVED) {
            throw new ResourceNotFoundException("Restaurant not found");
        }
        return RestaurantMapper.toResponse(restaurant);
    }

    @Transactional
    public RestaurantResponse create(RestaurantRequest request) {
        User owner = currentUserService.getCurrentUser();
        Restaurant restaurant = Restaurant.builder()
                .owner(owner)
                .name(request.name())
                .description(request.description())
                .cuisineType(request.cuisineType())
                .phone(request.phone())
                .email(request.email())
                .imageUrl(request.imageUrl())
                .city(request.city())
                .status(autoApproveRestaurants ? RestaurantStatus.APPROVED : RestaurantStatus.PENDING_APPROVAL)
                .open(autoApproveRestaurants)
                .build();
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest request) {
        Restaurant restaurant = ownedOrAdminRestaurant(id);
        restaurant.setName(request.name());
        restaurant.setDescription(request.description());
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setPhone(request.phone());
        restaurant.setEmail(request.email());
        restaurant.setImageUrl(request.imageUrl());
        restaurant.setCity(request.city());
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional
    public void delete(Long id) {
        Restaurant restaurant = ownedOrAdminRestaurant(id);
        restaurant.setOpen(false);
        restaurant.setStatus(RestaurantStatus.SUSPENDED);
        restaurantRepository.save(restaurant);
    }

    @Transactional
    public RestaurantResponse setOpen(Long id, boolean open) {
        Restaurant restaurant = ownedRestaurant(id);
        if (restaurant.getStatus() != RestaurantStatus.APPROVED) {
            throw new UnauthorizedException("Restaurant must be approved before it can be opened");
        }
        restaurant.setOpen(open);
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    @Transactional(readOnly = true)
    public List<RestaurantResponse> ownerRestaurants() {
        User owner = currentUserService.getCurrentUser();
        return restaurantRepository.findByOwnerId(owner.getId()).stream()
                .map(RestaurantMapper::toResponse)
                .toList();
    }

    @Transactional
    public RestaurantResponse changeStatus(Long id, RestaurantStatus status) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        restaurant.setStatus(status);
        if (status != RestaurantStatus.APPROVED) {
            restaurant.setOpen(false);
        }
        return RestaurantMapper.toResponse(restaurantRepository.save(restaurant));
    }

    public Restaurant ownedRestaurantEntity(Long id) {
        return ownedRestaurant(id);
    }

    private Restaurant ownedRestaurant(Long id) {
        User user = currentUserService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        if (!restaurant.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedException("You can manage only your own restaurant");
        }
        return restaurant;
    }

    private Restaurant ownedOrAdminRestaurant(Long id) {
        User user = currentUserService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        if (!restaurant.getOwner().getId().equals(user.getId()) && !currentUserService.hasRole(user, RoleName.ADMIN)) {
            throw new UnauthorizedException("You can manage only your own restaurant");
        }
        return restaurant;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
