package com.attendance.scheduler.notification.repository;

import com.attendance.scheduler.notification.domain.Notification;
import com.attendance.scheduler.teacher.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
    Notification findNotificationEntityByTeacherEntityAndId(Teacher teacherEntity, Long id);

    List<Notification> findByTeacherEntity(Teacher teacherEntity);
}
