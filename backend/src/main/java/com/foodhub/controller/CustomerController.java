package com.foodhub.controller;

import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.dto.user.AddressRequest;
import com.foodhub.dto.user.AddressResponse;
import com.foodhub.dto.user.ProfileUpdateRequest;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.service.DashboardService;
import com.foodhub.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    private final UserService userService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.customerDashboard();
    }

    @GetMapping("/profile")
    public UserResponse profile() {
        return userService.profile();
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateProfile(request);
    }

    @GetMapping("/addresses")
    public List<AddressResponse> addresses() {
        return userService.addresses();
    }

    @PostMapping("/addresses")
    public AddressResponse addAddress(@Valid @RequestBody AddressRequest request) {
        return userService.addAddress(request);
    }

    @PutMapping("/addresses/{id}")
    public AddressResponse updateAddress(@PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return userService.updateAddress(id, request);
    }

    @DeleteMapping("/addresses/{id}")
    public void deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
    }

    @PutMapping("/addresses/{id}/default")
    public AddressResponse setDefaultAddress(@PathVariable Long id) {
        return userService.setDefaultAddress(id);
    }
}

