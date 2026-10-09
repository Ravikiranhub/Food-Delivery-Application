package com.foodhub.service;

import com.foodhub.dto.dashboard.DashboardResponse;
import com.foodhub.entity.User;
import com.foodhub.enums.OrderStatus;
import com.foodhub.enums.DeliveryStatus;
import com.foodhub.enums.RestaurantStatus;
import com.foodhub.enums.RoleName;
import com.foodhub.repository.DeliveryRepository;
import com.foodhub.repository.CustomerOrderRepository;
import com.foodhub.repository.DeliveryPartnerRepository;
import com.foodhub.repository.FoodItemRepository;
import com.foodhub.repository.OrderItemRepository;
import com.foodhub.repository.RestaurantRepository;
import com.foodhub.repository.UserRepository;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final FoodItemRepository foodItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public DashboardResponse customerDashboard() {
        User user = currentUserService.getCurrentUser();
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("totalOrders", orderRepository.countByCustomerId(user.getId()));
        return new DashboardResponse(counts, Map.of(), Map.of());
    }

    @Transactional(readOnly = true)
    public DashboardResponse restaurantDashboard() {
        User owner = currentUserService.getCurrentUser();
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("totalOrders", orderRepository.countByRestaurant_Owner_Id(owner.getId()));
        counts.put("restaurants", (long) restaurantRepository.findByOwnerId(owner.getId()).size());

        Map<String, BigDecimal> money = new LinkedHashMap<>();
        money.put("revenue", orderRepository.sumOwnerRevenue(owner.getId(), OrderStatus.DELIVERED));

        Map<String, Object> highlights = new LinkedHashMap<>();
        highlights.put("popularFoods", orderItemRepository.popularFoodsByOwner(owner.getId()).stream()
                .limit(5)
                .map(row -> Map.of("name", row[0], "quantity", row[1]))
                .toList());

        return new DashboardResponse(counts, money, highlights);
    }

    @Transactional(readOnly = true)
    public DashboardResponse deliveryDashboard() {
        User user = currentUserService.getCurrentUser();
        var partner = deliveryPartnerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new com.foodhub.exception.ResourceNotFoundException("Delivery partner profile not found"));
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("totalDeliveries", deliveryRepository.countByDeliveryPartnerId(partner.getId()));
        counts.put("completedDeliveries", deliveryRepository.countByDeliveryPartnerIdAndStatus(partner.getId(), DeliveryStatus.DELIVERED));

        Map<String, BigDecimal> money = new LinkedHashMap<>();
        money.put("totalEarnings", partner.getTotalEarnings());
        return new DashboardResponse(counts, money, Map.of("available", partner.isAvailable()));
    }

    @Transactional(readOnly = true)
    public DashboardResponse adminDashboard() {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put("totalUsers", userRepository.count());
        counts.put("customers", userRepository.countByRole(RoleName.CUSTOMER));
        counts.put("restaurantOwners", userRepository.countByRole(RoleName.RESTAURANT_OWNER));
        counts.put("deliveryPartners", userRepository.countByRole(RoleName.DELIVERY_PARTNER));
        counts.put("totalRestaurants", restaurantRepository.count());
        counts.put("approvedRestaurants", restaurantRepository.countByStatus(RestaurantStatus.APPROVED));
        counts.put("totalFoodItems", foodItemRepository.count());
        counts.put("totalOrders", orderRepository.count());
        counts.put("activeDeliveryPartners", deliveryPartnerRepository.countByAvailableTrue());

        Map<String, BigDecimal> money = new LinkedHashMap<>();
        money.put("totalRevenue", orderRepository.sumTotalAmountByStatus(OrderStatus.DELIVERED));
        return new DashboardResponse(counts, money, Map.of());
    }
}
