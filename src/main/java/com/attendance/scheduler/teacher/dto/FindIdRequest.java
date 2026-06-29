package com.attendance.scheduler.teacher.dto;

import jakarta.validation.constraints.NotEmpty;

public record FindIdRequest(
        @NotEmpty(message = "이메일을 입력해주세요") String email
) {
    public FindIdRequest withEmail(String email) {
        return new FindIdRequest(email);
    }
}
