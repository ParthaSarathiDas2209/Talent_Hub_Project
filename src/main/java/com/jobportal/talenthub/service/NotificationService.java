package com.jobportal.talenthub.service;

import com.jobportal.talenthub.dto.NotificationResponseDto;
import com.jobportal.talenthub.entity.User;

import java.util.List;

public interface NotificationService {

    // Create a notification for a user
    void createNotification(User user, String message);

    // Get all notifications of a user
    List<NotificationResponseDto> getUserNotifications(User user);

    // Mark a notification as read
    void markAsRead(Long notificationId, User user);

    // Count unread notifications
    long countUnreadNotifications(User user);
}
