package com.foodhub.controller;

import com.foodhub.dto.common.PageResponse;
import com.foodhub.dto.review.ReviewRequest;
import com.foodhub.dto.review.ReviewResponse;
import com.foodhub.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ReviewResponse create(@Valid @RequestBody ReviewRequest request) {
        return reviewService.create(request);
    }

    @GetMapping("/restaurants/{id}/reviews")
    public PageResponse<ReviewResponse> restaurantReviews(@PathVariable Long id, Pageable pageable) {
        return reviewService.restaurantReviews(id, pageable);
    }

    @GetMapping("/food-items/{id}/reviews")
    public PageResponse<ReviewResponse> foodReviews(@PathVariable Long id, Pageable pageable) {
        return reviewService.foodReviews(id, pageable);
    }

    @DeleteMapping("/reviews/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}

