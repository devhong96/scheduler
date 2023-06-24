package com.attendance.scheduler.Dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class LoginDTO {

    @NotEmpty(message = "아이디를 입력해 주세요")
    private String teacherId;

    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;
}
