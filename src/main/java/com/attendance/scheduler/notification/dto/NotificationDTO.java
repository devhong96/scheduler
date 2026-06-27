package com.attendance.scheduler.notification.dto;

import com.attendance.scheduler.notification.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@NoArgsConstructor
public class NotificationDTO {

    private String message;

    private boolean checked;

    private LocalDateTime createdTime;

    public Notification toEntity(){
        return Notification.builder()
                .message(message)
                .createdTime(createdTime)
                .checked(checked)
                .build();
    }
}
