package com.foodhub.dto.user;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        Long id,
        String name,
        String email,
        String phone,
        boolean active,
        Set<String> roles,
        Instant createdAt
) {
}

