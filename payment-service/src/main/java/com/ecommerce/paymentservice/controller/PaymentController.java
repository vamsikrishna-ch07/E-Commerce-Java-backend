package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentInitiateRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.PaymentVerifyRequest;
import com.ecommerce.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private Long getUserId(Principal principal) {
        return Long.valueOf(principal.getName());
    }

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> initiatePayment(Principal principal, @RequestBody PaymentInitiateRequest request) {
        Long userId = getUserId(principal);
        request.setUserId(userId);
        PaymentResponse paymentResponse = paymentService.initiatePayment(request);
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        PaymentResponse paymentResponse = paymentService.verifyPayment(request);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PaymentResponse> getPaymentStatusByOrderId(@PathVariable Long orderId) {
        PaymentResponse paymentResponse = paymentService.getPaymentStatusByOrderId(orderId);
        return ResponseEntity.ok(paymentResponse);
    }
}
