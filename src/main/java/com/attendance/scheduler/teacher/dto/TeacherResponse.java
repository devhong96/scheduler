package com.attendance.scheduler.teacher.dto;

public record TeacherResponse(
        Long id,
        String username,
        String teacherName,
        boolean approved
) {
}
