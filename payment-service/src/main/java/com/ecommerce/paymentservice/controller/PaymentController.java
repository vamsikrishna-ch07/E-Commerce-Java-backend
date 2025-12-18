package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentInitiateRequest;
import com.ecommerce.paymentservice.dto.PaymentRequest; // Import PaymentRequest
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
        // In our setup, the 'sub' (subject) claim of the JWT is the user's ID.
        // Spring Security makes this available via principal.getName().
        return Long.valueOf(principal.getName());
    }

    // --- User-facing payment initiation and verification ---
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER')") // Changed to hasRole('USER') for user-initiated payments
    public ResponseEntity<PaymentResponse> initiatePayment(Principal principal, @RequestBody PaymentInitiateRequest request) {
        Long userId = getUserId(principal);
        request.setUserId(userId);
        PaymentResponse paymentResponse = paymentService.initiatePayment(request);
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasRole('USER')") // Changed to hasRole('USER') for user-initiated verification
    public ResponseEntity<PaymentResponse> verifyPayment(@RequestBody PaymentVerifyRequest request) {
        PaymentResponse paymentResponse = paymentService.verifyPayment(request);
        return ResponseEntity.ok(paymentResponse);
    }

    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasRole('USER')") // Changed to hasRole('USER') for user to check their own payment status
    public ResponseEntity<PaymentResponse> getPaymentStatusByOrderId(@PathVariable Long orderId) {
        // In a real app, you'd also verify that the orderId belongs to the authenticated user
        PaymentResponse paymentResponse = paymentService.getPaymentStatusByOrderId(orderId);
        return ResponseEntity.ok(paymentResponse);
    }

    // --- Internal service-to-service payment processing ---
    @PostMapping("/process")
    @PreAuthorize("hasRole('USER') or hasAuthority('SCOPE_internal.write')") // Protected for internal services like order-service
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse paymentResponse = paymentService.processPayment(request);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
}
