package com.attendance.scheduler.notification.application;

import com.attendance.scheduler.notification.domain.Notification;
import com.attendance.scheduler.notification.dto.NotificationResponse;
import com.attendance.scheduler.notification.repository.NotificationJpaRepository;
import com.attendance.scheduler.notification.repository.NotificationRepository;
import com.attendance.scheduler.teacher.domain.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationJpaRepository notificationJpaRepository;


    public void markAsRead(List<Notification> notificationEntity) {
        notificationEntity.forEach(Notification::checked);
    }

    public List<NotificationResponse> findByTeacherEntityOrderByCreatedDesc(Teacher teacherEntity) {
        return notificationRepository.findByTeacherEntityOrderByCreatedDesc(teacherEntity);
    }

    public void CheckedByTeacherEntity(Teacher teacherEntity, Long id) {
        Notification notificationEntity = notificationJpaRepository
                .findNotificationEntityByTeacherEntityAndId(teacherEntity, id);
        notificationEntity.checked();
        notificationJpaRepository.save(notificationEntity);
    }
}
