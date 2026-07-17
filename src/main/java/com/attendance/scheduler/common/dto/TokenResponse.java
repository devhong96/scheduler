package com.attendance.scheduler.common.dto;

import java.util.List;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String username,
        List<String> roles
) {
    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn,
                                   String username, List<String> roles) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn, username, roles);
    }
}
