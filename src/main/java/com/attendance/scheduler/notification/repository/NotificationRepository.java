package com.attendance.scheduler.notification.repository;

import com.attendance.scheduler.notification.dto.NotificationDTO;
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

    public List<NotificationDTO> findByTeacherEntityOrderByCreatedDesc(Teacher teacher) {
        return queryFactory
                .select(Projections.fields(NotificationDTO.class,
                        notification.message,
                        notification.checked,
                        notification.createdTime))
                .from(notification)
                .where(notification.teacherEntity.eq(teacher))
                .fetch();
    }


}
