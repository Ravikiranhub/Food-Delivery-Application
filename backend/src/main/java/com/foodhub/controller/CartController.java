package com.foodhub.controller;

import com.foodhub.dto.cart.AddCartItemRequest;
import com.foodhub.dto.cart.ApplyCouponRequest;
import com.foodhub.dto.cart.CartResponse;
import com.foodhub.dto.cart.UpdateCartItemRequest;
import com.foodhub.service.CartService;
import jakarta.validation.Valid;
import java.util.Map;
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
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse cart() {
        return cartService.getCart();
    }

    @PostMapping("/items")
    public CartResponse add(@Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{id}")
    public CartResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(id, request);
    }

    @DeleteMapping("/items/{id}")
    public CartResponse remove(@PathVariable Long id) {
        return cartService.removeItem(id);
    }

    @DeleteMapping
    public CartResponse clear() {
        return cartService.clearCart();
    }

    @PostMapping("/apply-coupon")
    public Map<String, Object> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return cartService.applyCoupon(request);
    }
}

