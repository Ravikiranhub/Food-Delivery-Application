package com.foodhub.service;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.entity.User;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.mapper.RestaurantMapper;
import com.foodhub.mapper.UserMapper;
import com.foodhub.repository.RestaurantRepository;
import com.foodhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminManagementService {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public PageResponse<UserResponse> users(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable).map(UserMapper::toResponse));
    }

    @Transactional
    public UserResponse setUserActive(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.foodhub.exception.ResourceNotFoundException("User not found"));
        user.setActive(active);
        return UserMapper.toResponse(userRepository.save(user));
    }

    public PageResponse<RestaurantResponse> restaurants(RestaurantStatus status, Pageable pageable) {
        return PageResponse.from(restaurantRepository.search(null, null, null, null, status, pageable)
                .map(RestaurantMapper::toResponse));
    }
}

