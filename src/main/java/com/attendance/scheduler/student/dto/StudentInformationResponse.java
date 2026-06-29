package com.attendance.scheduler.student.dto;

import java.time.LocalDateTime;

public record StudentInformationResponse(
        Long id,
        String studentName,
        String studentPhoneNumber,
        String studentAddress,
        String studentDetailedAddress,
        String studentParentPhoneNumber,
        String teacherName,
        LocalDateTime createdDate
) {
}
