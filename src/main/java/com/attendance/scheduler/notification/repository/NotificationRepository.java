package com.attendance.scheduler.notification.repository;

import com.attendance.scheduler.notification.dto.NotificationResponse;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.attendance.scheduler.notification.domain.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepository {

    public final JPAQueryFactory queryFactory;

    public List<NotificationResponse> findByTeacherEntityOrderByCreatedDesc(Teacher teacher) {
        return queryFactory
                .select(Projections.constructor(NotificationResponse.class,
                        notification.message,
                        notification.checked,
                        notification.createdDate))
                .from(notification)
                .where(notification.teacherEntity.eq(teacher))
                .fetch();
    }
}
