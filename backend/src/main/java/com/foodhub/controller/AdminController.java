package com.foodhub.controller;

import com.foodhub.dto.admin.CouponRequest;
import com.foodhub.dto.admin.CouponResponse;
import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.dto.order.OrderResponse;
import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.dto.user.UserResponse;
import com.foodhub.enums.OrderStatus;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.service.AdminManagementService;
import com.foodhub.service.CouponService;
import com.foodhub.service.DashboardService;
import com.foodhub.service.OrderService;
import com.foodhub.service.RestaurantService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final AdminManagementService adminManagementService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final CouponService couponService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.adminDashboard();
    }

    @GetMapping("/users")
    public PageResponse<UserResponse> users(Pageable pageable) {
        return adminManagementService.users(pageable);
    }

    @PatchMapping("/users/{id}/block")
    public UserResponse block(@PathVariable Long id) {
        return adminManagementService.setUserActive(id, false);
    }

    @PatchMapping("/users/{id}/unblock")
    public UserResponse unblock(@PathVariable Long id) {
        return adminManagementService.setUserActive(id, true);
    }

    @GetMapping("/restaurants")
    public PageResponse<RestaurantResponse> restaurants(@RequestParam(required = false) RestaurantStatus status, Pageable pageable) {
        return adminManagementService.restaurants(status, pageable);
    }

    @PatchMapping("/restaurants/{id}/approve")
    public RestaurantResponse approveRestaurant(@PathVariable Long id) {
        return restaurantService.changeStatus(id, RestaurantStatus.APPROVED);
    }

    @PatchMapping("/restaurants/{id}/reject")
    public RestaurantResponse rejectRestaurant(@PathVariable Long id) {
        return restaurantService.changeStatus(id, RestaurantStatus.REJECTED);
    }

    @PatchMapping("/restaurants/{id}/suspend")
    public RestaurantResponse suspendRestaurant(@PathVariable Long id) {
        return restaurantService.changeStatus(id, RestaurantStatus.SUSPENDED);
    }

    @GetMapping("/orders")
    public PageResponse<OrderResponse> orders(@RequestParam(required = false) OrderStatus status, Pageable pageable) {
        return orderService.listOrders(status, pageable);
    }

    @GetMapping("/coupons")
    public List<CouponResponse> coupons() {
        return couponService.list();
    }

    @PostMapping("/coupons")
    public CouponResponse createCoupon(@Valid @RequestBody CouponRequest request) {
        return couponService.create(request);
    }

    @PutMapping("/coupons/{id}")
    public CouponResponse updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return couponService.update(id, request);
    }

    @PatchMapping("/coupons/{id}/activate")
    public CouponResponse activateCoupon(@PathVariable Long id) {
        return couponService.setActive(id, true);
    }

    @PatchMapping("/coupons/{id}/deactivate")
    public CouponResponse deactivateCoupon(@PathVariable Long id) {
        return couponService.setActive(id, false);
    }

    @DeleteMapping("/coupons/{id}")
    public void deleteCoupon(@PathVariable Long id) {
        couponService.delete(id);
    }

    @GetMapping("/reports")
    public DashboardResponse reports() {
        return dashboardService.adminDashboard();
    }
}

