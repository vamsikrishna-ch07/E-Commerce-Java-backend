package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.NotificationRequest;

public interface NotificationService {
    void sendNotification(NotificationRequest request);
}
