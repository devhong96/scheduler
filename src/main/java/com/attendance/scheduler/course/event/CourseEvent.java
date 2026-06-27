package com.attendance.scheduler.course.event;

import com.attendance.scheduler.teacher.domain.Teacher;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CourseEvent {
    private final Teacher teacherEntity;
    private final String message;
}
