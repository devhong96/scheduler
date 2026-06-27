package com.attendance.scheduler.teacher.dto;

import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class JoinTeacherDTO {

    @NotEmpty(message = "아이디를 입력해 주세요")
    private String username;

    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;

    @NotEmpty(message = "이름을 입력해 주세요")
    private String teacherName;

    @NotEmpty(message = "이메일을 입력해 주세요")
    private String email;

    private boolean approved;

    public static TeacherDTO getInstance(){
        return new TeacherDTO();
    }

    public Teacher toEntity(){
        return Teacher.builder()
                .username(username)
                .password(password)
                .email(email)
                .teacherName(teacherName)
                .approved(approved)
                .build();
    }
}
