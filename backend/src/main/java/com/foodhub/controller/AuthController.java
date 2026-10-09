package com.foodhub.controller;

import com.foodhub.dto.auth.AuthResponse;
import com.foodhub.dto.auth.LoginRequest;
import com.foodhub.dto.auth.RegisterRequest;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.enums.RoleName;
import com.foodhub.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request, null);
    }

    @PostMapping("/register/restaurant-owner")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerRestaurantOwner(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request, RoleName.RESTAURANT_OWNER);
    }

    @PostMapping("/register/delivery-partner")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerDeliveryPartner(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request, RoleName.DELIVERY_PARTNER);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me() {
        return authService.currentUser();
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "Logged out on client");
    }
}

