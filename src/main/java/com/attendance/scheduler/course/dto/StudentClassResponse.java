package com.attendance.scheduler.course.dto;

public record StudentClassResponse(
        String studentName,
        Integer monday,
        Integer tuesday,
        Integer wednesday,
        Integer thursday,
        Integer friday
) {
}
