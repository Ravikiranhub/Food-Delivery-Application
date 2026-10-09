package com.foodhub.controller;

import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public DashboardResponse customer() {
        return dashboardService.customerDashboard();
    }

    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public DashboardResponse restaurant() {
        return dashboardService.restaurantDashboard();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public DashboardResponse admin() {
        return dashboardService.adminDashboard();
    }
}

