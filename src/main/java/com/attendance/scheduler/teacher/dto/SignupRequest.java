package com.attendance.scheduler.teacher.dto;

import jakarta.validation.constraints.NotEmpty;

/**
 * 교사 회원가입 요청. 승인여부(approved)는 클라이언트가 정하지 못하도록 제외한다(항상 미승인으로 생성).
 */
public record SignupRequest(
        @NotEmpty(message = "아이디를 입력해 주세요") String username,
        @NotEmpty(message = "비밀번호를 입력해 주세요") String password,
        @NotEmpty(message = "이름을 입력해 주세요") String teacherName,
        @NotEmpty(message = "이메일을 입력해 주세요") String email
) {
    public JoinTeacherRequest toJoinRequest() {
        return new JoinTeacherRequest(username, password, teacherName, email, false);
    }
}
