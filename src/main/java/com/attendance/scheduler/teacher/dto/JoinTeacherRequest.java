package com.attendance.scheduler.teacher.dto;

import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.validation.constraints.NotEmpty;

public record JoinTeacherRequest(
        @NotEmpty(message = "아이디를 입력해 주세요") String username,
        @NotEmpty(message = "비밀번호를 입력해 주세요") String password,
        @NotEmpty(message = "이름을 입력해 주세요") String teacherName,
        @NotEmpty(message = "이메일을 입력해 주세요") String email,
        boolean approved
) {
    public JoinTeacherRequest withUsername(String username) {
        return new JoinTeacherRequest(username, password, teacherName, email, approved);
    }

    public JoinTeacherRequest withPassword(String password) {
        return new JoinTeacherRequest(username, password, teacherName, email, approved);
    }

    public JoinTeacherRequest withTeacherName(String teacherName) {
        return new JoinTeacherRequest(username, password, teacherName, email, approved);
    }

    public JoinTeacherRequest withEmail(String email) {
        return new JoinTeacherRequest(username, password, teacherName, email, approved);
    }

    public JoinTeacherRequest withApproved(boolean approved) {
        return new JoinTeacherRequest(username, password, teacherName, email, approved);
    }

    public Teacher toEntity() {
        return Teacher.builder()
                .username(username)
                .password(password)
                .email(email)
                .teacherName(teacherName)
                .approved(approved)
                .build();
    }
}
