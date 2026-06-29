package com.attendance.scheduler.admin.dto;

import com.attendance.scheduler.admin.domain.Admin;
import jakarta.validation.constraints.NotEmpty;

public record AdminAccountResponse(
        @NotEmpty(message = "아이디를 입력해 주세요") String username,
        @NotEmpty(message = "비밀번호를 입력해 주세요") String password,
        String name,
        String email
) {
    public Admin toEntity() {
        return Admin.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .build();
    }
}
