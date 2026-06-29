package com.attendance.scheduler.admin.dto;

public record ChangeTeacherRequest(
        Long teacherId,
        Long studentId
) {
}
