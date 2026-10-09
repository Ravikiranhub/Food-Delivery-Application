package com.foodhub.dto.auth;

import com.foodhub.dto.user.UserResponse;

public record AuthResponse(
        String tokenType,
        String accessToken,
        long expiresInMs,
        UserResponse user
) {
}

