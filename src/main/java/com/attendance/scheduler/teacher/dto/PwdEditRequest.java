package com.attendance.scheduler.teacher.dto;

import jakarta.validation.constraints.NotEmpty;

public record PwdEditRequest(
        String username,
        @NotEmpty(message = "변경할 비밀번호를 입력해 주세요.") String password
) {
    public PwdEditRequest withUsername(String username) {
        return new PwdEditRequest(username, this.password);
    }

    public PwdEditRequest withPassword(String password) {
        return new PwdEditRequest(this.username, password);
    }
}
