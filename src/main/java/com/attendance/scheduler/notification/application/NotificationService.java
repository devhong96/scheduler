package com.attendance.scheduler.notification.application;

import com.attendance.scheduler.notification.domain.NotificationEntity;
import com.attendance.scheduler.notification.dto.NotificationDTO;
import com.attendance.scheduler.notification.repository.NotificationJpaRepository;
import com.attendance.scheduler.notification.repository.NotificationRepository;
import com.attendance.scheduler.teacher.domain.TeacherEntity;
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


    public void markAsRead(List<NotificationEntity> notificationEntity) {
        notificationEntity.forEach(NotificationEntity::checked);
    }

    public List<NotificationDTO> findByTeacherEntityOrderByCreatedDesc(TeacherEntity teacherEntity) {
        return notificationRepository.findByTeacherEntityOrderByCreatedDesc(teacherEntity);
    }

    public void CheckedByTeacherEntity(TeacherEntity teacherEntity, Long id) {
        NotificationEntity notificationEntity = notificationJpaRepository
                .findNotificationEntityByTeacherEntityAndId(teacherEntity, id);
        notificationEntity.checked();
        notificationJpaRepository.save(notificationEntity);
    }
}
