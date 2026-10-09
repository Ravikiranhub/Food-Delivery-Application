package com.foodhub.controller;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.dto.order.OrderResponse;
import com.foodhub.dto.order.UpdateOrderStatusRequest;
import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.enums.OrderStatus;
import com.foodhub.service.DashboardService;
import com.foodhub.service.OrderService;
import com.foodhub.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class OwnerController {

    private final DashboardService dashboardService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.restaurantDashboard();
    }

    @GetMapping("/restaurants")
    public List<RestaurantResponse> restaurants() {
        return restaurantService.ownerRestaurants();
    }

    @GetMapping("/orders")
    public PageResponse<OrderResponse> orders(Pageable pageable) {
        return orderService.listOrders(null, pageable);
    }

    @PostMapping("/orders/{id}/accept")
    public OrderResponse accept(@PathVariable Long id) {
        return orderService.updateStatus(id, new UpdateOrderStatusRequest(OrderStatus.CONFIRMED, "Accepted by restaurant"));
    }

    @PostMapping("/orders/{id}/reject")
    public OrderResponse reject(@PathVariable Long id) {
        return orderService.updateStatus(id, new UpdateOrderStatusRequest(OrderStatus.REJECTED, "Rejected by restaurant"));
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse status(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }
}

