package com.attendance.scheduler.notification.event;

import com.attendance.scheduler.course.event.CourseEvent;
import com.attendance.scheduler.notification.domain.Notification;
import com.attendance.scheduler.notification.repository.NotificationJpaRepository;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
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
    private final TeacherJpaRepository teacherJpaRepository;

    @EventListener
    public void handleCourseEvent(CourseEvent courseEvent) {
        if (courseEvent.getTeacherEntity() == null) {
            return;
        }
        // 비동기(별도 트랜잭션)로 실행되므로 이벤트의 Teacher 는 detached 상태다.
        // 지연 컬렉션 접근/detached 참조 문제를 피하기 위해 id 로 관리되는 프록시를 얻어 FK 만 채운다.
        Teacher teacherRef = teacherJpaRepository.getReferenceById(courseEvent.getTeacherEntity().getId());

        Notification entity = Notification.builder()
                .teacherEntity(teacherRef)
                .message(courseEvent.getMessage())
                .checked(false)
                .build();

        notificationJpaRepository.save(entity);
    }
}
