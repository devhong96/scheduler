package com.attendance.scheduler.admin.dto;

import jakarta.validation.constraints.NotEmpty;

public record FindIdResponse(
        @NotEmpty(message = "이메일을 입력해주세요") String email,
        String username
) {
}
