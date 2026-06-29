package com.attendance.scheduler.infra.auth;

import java.time.LocalDateTime;

/**
 * 세션에 저장되는 1회용 인증번호와 만료 시각.
 */
public record AuthCode(String code, LocalDateTime expiresAt) {

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }
}