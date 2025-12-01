package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.client.OrderClient;
import com.ecommerce.paymentservice.dto.OrderStatusUpdateRequest;
import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.repo.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    @Override
    @Transactional
    public Payment makePayment(PaymentRequest request) {

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .transactionId(UUID.randomUUID().toString())
                .status(PaymentStatus.SUCCESS) // Simulate successful payment
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Simulate asynchronous webhook call to update order status
        // In a real-world scenario, this would be handled by a message queue
        // or a dedicated webhook service that retries on failure.
        try {
            orderClient.updateOrderStatus(
                    savedPayment.getOrderId(),
                    OrderStatusUpdateRequest.builder().status(savedPayment.getStatus().name()).build()
            );
        } catch (Exception e) {
            // Log the error, potentially trigger a retry mechanism
            System.err.println("Failed to update order status for orderId: " + savedPayment.getOrderId() + ". Error: " + e.getMessage());
            // Optionally, update payment status to FAILED or PENDING_RETRY if the webhook fails
        }

        return savedPayment;
    }
}
