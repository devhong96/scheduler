package com.attendance.scheduler.teacher.dto;

import jakarta.validation.constraints.NotEmpty;

public record EditEmailRequest(
        String username,
        @NotEmpty(message = "이메일을 입력해 주세요") String email
) {
    public EditEmailRequest withUsername(String username) {
        return new EditEmailRequest(username, this.email);
    }
}
