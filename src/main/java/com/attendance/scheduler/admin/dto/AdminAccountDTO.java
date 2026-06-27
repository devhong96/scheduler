package com.attendance.scheduler.admin.dto;

import com.attendance.scheduler.admin.domain.Admin;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class AdminAccountDTO {
    @NotEmpty(message = "아이디를 입력해 주세요")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;

    private String name;

    private String email;

    public Admin toEntity() {
        return Admin.builder()
                .username(username)
                .password(password)
                .name(name)
                .email(email)
                .build();
    }
}
