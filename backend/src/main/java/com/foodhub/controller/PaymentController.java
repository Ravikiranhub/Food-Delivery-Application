package com.foodhub.controller;

import com.foodhub.dto.payment.PaymentRequest;
import com.foodhub.dto.payment.PaymentResponse;
import com.foodhub.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public PaymentResponse confirmMockPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.getByOrder(request.orderId());
    }

    @GetMapping("/payments/{id}")
    public PaymentResponse getPayment(@PathVariable Long id) {
        return paymentService.getPayment(id);
    }

    @GetMapping("/orders/{orderId}/payment")
    public PaymentResponse getOrderPayment(@PathVariable Long orderId) {
        return paymentService.getByOrder(orderId);
    }
}

