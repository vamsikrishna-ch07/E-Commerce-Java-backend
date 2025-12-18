package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.NotificationRequest;
import com.ecommerce.notificationservice.dto.NotificationResponse;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody(), true); // true indicates the body is HTML

            mailSender.send(mimeMessage);
            return NotificationResponse.builder()
                    .status("SUCCESS")
                    .message("Notification sent successfully")
                    .recipient(request.getTo())
                    .build();
        } catch (Exception e) {
            // In a real app, you'd have more robust error handling, like a retry queue
            // Log the error
            System.err.println("Failed to send email to " + request.getTo() + ". Error: " + e.getMessage());
            return NotificationResponse.builder()
                    .status("FAILED")
                    .message("Failed to send notification: " + e.getMessage())
                    .recipient(request.getTo())
                    .build();
        }
    }
}
