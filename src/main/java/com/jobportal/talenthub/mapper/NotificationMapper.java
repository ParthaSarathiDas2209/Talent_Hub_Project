package com.jobportal.talenthub.mapper;

import com.jobportal.talenthub.dto.NotificationResponseDto;
import com.jobportal.talenthub.entity.Notification;

public class NotificationMapper {

    public static NotificationResponseDto notificationResponseDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}