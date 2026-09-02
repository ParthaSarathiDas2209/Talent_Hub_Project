package com.jobportal.talenthub.service.impl;

import com.jobportal.talenthub.dto.NotificationResponseDto;
import com.jobportal.talenthub.entity.Notification;
import com.jobportal.talenthub.entity.User;
import com.jobportal.talenthub.exception.AccessDeniedException;
import com.jobportal.talenthub.exception.ResourceNotFoundException;
import com.jobportal.talenthub.mapper.NotificationMapper;
import com.jobportal.talenthub.repository.NotificationRepository;
import com.jobportal.talenthub.service.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void createNotification(User user, String message) {
        Notification notification = new Notification();

        notification.setUser(user);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDto> getUserNotifications(User user) {

        List<Notification> notifications =
                notificationRepository
                        .findAllByUserOrderByCreatedAtDesc(
                                user
                        );

        return notifications
                .stream()
                .map(NotificationMapper::notificationResponseDto)
                .toList();
    }
    
    @Override
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found with id : "
                                        + notificationId
                        )
                );

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to mark this notification as read."
            );
        }

        notification.setRead(true);
        notificationRepository.save(notification);

    }

    @Override
    public long countUnreadNotifications(User user) {
        return notificationRepository
                .countByUserAndIsReadFalse(user);
    }
}