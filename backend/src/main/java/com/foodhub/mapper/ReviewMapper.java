package com.foodhub.mapper;

import com.foodhub.dto.review.ReviewResponse;
import com.foodhub.entity.Review;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getCustomer().getId(),
                review.getCustomer().getName(),
                review.getRestaurant().getId(),
                review.getFoodItem() == null ? null : review.getFoodItem().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}

