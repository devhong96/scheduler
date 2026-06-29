package com.attendance.scheduler.common.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "아이디를 입력해 주세요") String username,
        @NotEmpty(message = "비밀번호를 입력해 주세요") String password
) {
    public LoginRequest withUsername(String username) {
        return new LoginRequest(username, this.password);
    }

    public LoginRequest withPassword(String password) {
        return new LoginRequest(this.username, password);
    }
}
