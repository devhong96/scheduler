package com.attendance.scheduler.common.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest(
        @NotEmpty(message = "refreshToken 이 필요합니다") String refreshToken
) {
}
