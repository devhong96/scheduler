package com.attendance.scheduler.admin.dto;

import jakarta.validation.constraints.NotEmpty;

public record CertRequest(
        String username,
        @NotEmpty(message = "인증번호를 입력해 주세요") String authNum
) {
}
