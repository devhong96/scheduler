package com.attendance.scheduler.common.controller;

import com.attendance.scheduler.admin.application.AdminCertService;
import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.admin.dto.EmailResponse;
import com.attendance.scheduler.teacher.application.TeacherCertService;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.EditEmailRequest;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 로그인한 본인 계정 관리 REST API (/api/account) — 비밀번호/이메일 변경.
 * 로그인 주체가 교사인지 관리자인지에 따라 알맞은 서비스로 위임한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountApiController {

    private final TeacherJpaRepository teacherJpaRepository;
    private final TeacherCertService teacherCertService;
    private final AdminCertService adminCertService;
    private final AdminService adminService;

    /** 현재 로그인 계정 정보 (아이디, 이메일, 역할). */
    @GetMapping
    public ResponseEntity<?> account(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("username", username);

        Teacher teacher = teacherJpaRepository.findByUsernameIs(username);
        if (teacher != null) {
            body.put("email", teacher.getEmail());
            body.put("role", "TEACHER");
        } else {
            Optional<EmailResponse> admin = adminService.findAdminEmailByID(new EmailResponse(username, ""));
            body.put("email", admin.map(EmailResponse::email).orElse(""));
            body.put("role", "ADMIN");
        }
        return ResponseEntity.ok(body);
    }

    /** 비밀번호 변경. */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PwdEditRequest request, Authentication authentication) {
        String username = authentication.getName();
        PwdEditRequest req = request.withUsername(username);
        if (isTeacher(username)) {
            teacherCertService.initializePassword(req);
        } else {
            adminCertService.initializePassword(req);
        }
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }

    /** 이메일 변경. */
    @PostMapping("/change-email")
    public ResponseEntity<?> changeEmail(@Valid @RequestBody EditEmailRequest request, Authentication authentication) {
        String username = authentication.getName();
        if (isTeacher(username)) {
            teacherCertService.updateEmail(request.withUsername(username));
        } else {
            adminCertService.updateEmail(
                    new com.attendance.scheduler.admin.dto.EditEmailRequest(username, request.email()));
        }
        return ResponseEntity.ok(Map.of("message", "이메일이 변경되었습니다."));
    }

    private boolean isTeacher(String username) {
        return teacherJpaRepository.findByUsernameIs(username) != null;
    }
}
