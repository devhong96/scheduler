package com.attendance.scheduler.course.dto;

import java.time.LocalDateTime;

public record ClassResponse(
        String studentName,
        Integer monday,
        Integer tuesday,
        Integer wednesday,
        Integer thursday,
        Integer friday,
        String teacherName,
        LocalDateTime lastModifiedDate
) {
}
