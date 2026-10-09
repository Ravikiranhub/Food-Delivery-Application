package com.foodhub.entity;

import com.foodhub.enums.RestaurantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "restaurants",
        uniqueConstraints = @UniqueConstraint(name = "uk_owner_restaurant_name", columnNames = {"owner_id", "name"}),
        indexes = {
                @Index(name = "idx_restaurants_city", columnList = "city"),
                @Index(name = "idx_restaurants_cuisine_type", columnList = "cuisine_type"),
                @Index(name = "idx_restaurants_status", columnList = "status"),
                @Index(name = "idx_restaurants_open", columnList = "is_open")
        }
)
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(length = 800)
    private String description;

    @Column(name = "cuisine_type", nullable = false, length = 80)
    private String cuisineType;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(name = "image_url", length = 600)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private RestaurantStatus status;

    @Column(name = "is_open", nullable = false)
    private boolean open;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        status = status == null ? RestaurantStatus.PENDING_APPROVAL : status;
        averageRating = averageRating == null ? BigDecimal.ZERO : averageRating;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}

