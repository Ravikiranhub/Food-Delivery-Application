package com.foodhub.service;

import com.foodhub.dto.payment.PaymentResponse;
import com.foodhub.entity.Payment;
import com.foodhub.exception.ResourceNotFoundException;
import com.foodhub.exception.UnauthorizedException;
import com.foodhub.mapper.PaymentMapper;
import com.foodhub.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;
    private final OrderService orderService;

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        if (!orderService.canAccess(payment.getOrder(), currentUserService.getCurrentUser())) {
            throw new UnauthorizedException("You cannot access this payment");
        }
        return PaymentMapper.toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByOrder(Long orderId) {
        orderService.findAccessibleOrder(orderId);
        return paymentRepository.findByOrderId(orderId)
                .map(PaymentMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }
}

