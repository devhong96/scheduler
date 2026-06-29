package com.attendance.scheduler.notification.event;

import com.attendance.scheduler.course.event.CourseEvent;
import com.attendance.scheduler.notification.domain.Notification;
import com.attendance.scheduler.notification.repository.NotificationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class CourseEventListener {

    private final NotificationJpaRepository notificationJpaRepository;

    @EventListener
    public void handleCourseEvent(CourseEvent courseEvent) {

        Notification entity = Notification.builder()
                .teacherEntity(courseEvent.getTeacherEntity())
                .message(courseEvent.getMessage())
                .checked(false)
                .build();
        entity.setTeacherEntity(courseEvent.getTeacherEntity());

        notificationJpaRepository.save(entity);
    }
}
