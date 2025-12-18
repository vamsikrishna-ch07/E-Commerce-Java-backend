package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.client.OrderClient;
import com.ecommerce.paymentservice.dto.OrderStatusUpdateRequest;
import com.ecommerce.paymentservice.dto.PaymentInitiateRequest;
import com.ecommerce.paymentservice.dto.PaymentRequest; // Import PaymentRequest
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.dto.PaymentVerifyRequest;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    // Helper to map Payment entity to PaymentResponse DTO
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .transactionId(payment.getTransactionId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .failureReason(payment.getFailureReason())
                .build();
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(PaymentInitiateRequest request) {
        // In a real scenario, this would interact with a payment gateway (e.g., Stripe, PayPal)
        // For now, we simulate a pending payment and generate a dummy transaction ID.

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.PENDING)
                .transactionId(UUID.randomUUID().toString()) // Simulate gateway transaction ID
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Optionally, update order status to PAYMENT_PENDING in OrderService
        // Note: The order-service's checkout method handles the main order status updates.
        // This call might be redundant or need careful coordination.
        // For now, let's assume order-service manages the primary status flow.
        // orderClient.updateOrderStatus(savedPayment.getOrderId(),
        //         OrderStatusUpdateRequest.builder().status(OrderStatus.PENDING).build());

        return mapToPaymentResponse(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(PaymentVerifyRequest request) {
        // In a real scenario, this would call the payment gateway to verify the transaction ID
        // and confirm the payment status.
        // For now, we simulate a successful verification.

        Payment payment = paymentRepository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Payment not found for transaction ID: " + request.getTransactionId()));

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now()); // Update payment date on completion
            Payment updatedPayment = paymentRepository.save(payment);

            // Update order status to PROCESSING in OrderService
            // Note: The order-service's checkout method handles the main order status updates.
            // This call might be redundant or need careful coordination.
            // For now, let's assume order-service manages the primary status flow.
            // orderClient.updateOrderStatus(updatedPayment.getOrderId(),
            //         OrderStatusUpdateRequest.builder().status(OrderStatus.PROCESSING).build());

            return mapToPaymentResponse(updatedPayment);
        } else {
            throw new RuntimeException("Payment already processed or in a final state: " + payment.getStatus());
        }
    }

    @Override
    public PaymentResponse getPaymentStatusByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));
        return mapToPaymentResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // This method is called by internal services (e.g., order-service) for direct payment processing.
        // It simulates a successful payment.

        // In a real application, you would integrate with a payment gateway here.
        // For simplicity, we'll assume it's always successful for internal calls.

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                // userId is not part of PaymentRequest, so we can't set it directly here.
                // It should ideally come from the order details or be passed in the request.
                // For now, we'll leave it null or fetch it if needed.
                .amount(request.getAmount())
                .paymentMethod("INTERNAL_SERVICE_CALL") // Indicate payment initiated by service
                .paymentDate(LocalDateTime.now())
                .status(PaymentStatus.COMPLETED) // Assume successful for internal processing
                .transactionId(UUID.randomUUID().toString()) // Generate a dummy transaction ID
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // No need to call orderClient.updateOrderStatus here, as order-service is the caller
        // and will update its own status based on the success/failure of this call.

        return mapToPaymentResponse(savedPayment);
    }
}
