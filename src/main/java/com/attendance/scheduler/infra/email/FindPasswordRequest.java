package com.attendance.scheduler.infra.email;

import jakarta.validation.constraints.NotEmpty;

public record FindPasswordRequest(
        @NotEmpty(message = "이메일을 입력해 주세요") String email,
        @NotEmpty(message = "아이디을 입력해 주세요") String username
) {
}
