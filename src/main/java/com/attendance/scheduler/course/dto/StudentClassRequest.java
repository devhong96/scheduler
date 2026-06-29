package com.attendance.scheduler.course.dto;

import jakarta.validation.constraints.NotEmpty;

public record StudentClassRequest(
        @NotEmpty(message = "학생 이름을 정확히 입력해 주세요") String studentName
) {
    public StudentClassRequest withStudentName(String studentName) {
        return new StudentClassRequest(studentName);
    }
}
