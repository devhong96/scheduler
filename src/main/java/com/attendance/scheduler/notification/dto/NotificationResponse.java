package com.attendance.scheduler.notification.dto;

import com.attendance.scheduler.notification.domain.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        boolean checked,
        LocalDateTime createdDate
) {
    public Notification toEntity() {
        return Notification.builder()
                .message(message)
                .checked(checked)
                .build();
    }
}
