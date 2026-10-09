package com.foodhub.controller;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.restaurant.RestaurantRequest;
import com.foodhub.dto.restaurant.RestaurantResponse;
import com.foodhub.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public PageResponse<RestaurantResponse> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean open,
            Pageable pageable
    ) {
        return restaurantService.listApproved(keyword, cuisine, city, open, pageable);
    }

    @GetMapping("/search")
    public PageResponse<RestaurantResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Boolean open,
            Pageable pageable
    ) {
        return restaurantService.listApproved(keyword, cuisine, city, open, pageable);
    }

    @GetMapping("/{id}")
    public RestaurantResponse get(@PathVariable Long id) {
        return restaurantService.getPublic(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public RestaurantResponse create(@Valid @RequestBody RestaurantRequest request) {
        return restaurantService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    public RestaurantResponse update(@PathVariable Long id, @Valid @RequestBody RestaurantRequest request) {
        return restaurantService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER','ADMIN')")
    public void delete(@PathVariable Long id) {
        restaurantService.delete(id);
    }

    @PatchMapping("/{id}/open-status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public RestaurantResponse setOpen(@PathVariable Long id, @RequestParam boolean open) {
        return restaurantService.setOpen(id, open);
    }
}

