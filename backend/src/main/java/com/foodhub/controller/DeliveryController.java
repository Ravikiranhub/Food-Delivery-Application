package com.foodhub.controller;

import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.dto.delivery.DeliveryResponse;
import com.foodhub.service.DashboardService;
import com.foodhub.service.DeliveryService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DELIVERY_PARTNER')")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return dashboardService.deliveryDashboard();
    }

    @GetMapping("/available-orders")
    public List<DeliveryResponse> availableOrders() {
        return deliveryService.availableOrders();
    }

    @GetMapping("/assigned-orders")
    public List<DeliveryResponse> assignedOrders() {
        return deliveryService.assignedOrders();
    }

    @PostMapping("/orders/{orderId}/accept")
    public DeliveryResponse accept(@PathVariable Long orderId) {
        return deliveryService.accept(orderId);
    }

    @PostMapping("/orders/{orderId}/picked-up")
    public DeliveryResponse pickedUp(@PathVariable Long orderId) {
        return deliveryService.pickedUp(orderId);
    }

    @PostMapping("/orders/{orderId}/delivered")
    public DeliveryResponse delivered(@PathVariable Long orderId) {
        return deliveryService.delivered(orderId);
    }

    @GetMapping("/history")
    public List<DeliveryResponse> history() {
        return deliveryService.history();
    }

    @GetMapping("/earnings")
    public Map<String, Object> earnings() {
        return deliveryService.earnings();
    }

    @PatchMapping("/availability")
    public Map<String, Object> availability(@RequestParam boolean available) {
        return deliveryService.setAvailability(available);
    }
}
