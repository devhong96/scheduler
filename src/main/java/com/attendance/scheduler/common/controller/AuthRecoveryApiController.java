package com.attendance.scheduler.common.controller;

import com.attendance.scheduler.infra.auth.AuthCodeService;
import com.attendance.scheduler.infra.auth.AuthVerification;
import com.attendance.scheduler.infra.email.EmailService;
import com.attendance.scheduler.infra.email.FindPasswordRequest;
import com.attendance.scheduler.teacher.application.TeacherCertService;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.CertRequest;
import com.attendance.scheduler.teacher.dto.FindIdRequest;
import com.attendance.scheduler.teacher.dto.FindIdResponse;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
import com.attendance.scheduler.teacher.dto.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 회원가입 및 계정 복구(아이디/비밀번호 찾기) REST API — 공개.
 * 기존 JoinController / TeacherCertController(member) 의 Thymeleaf 흐름을 이관.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthRecoveryApiController {

    private final TeacherService teacherService;
    private final TeacherCertService teacherCertService;
    private final AuthCodeService authCodeService;
    private final EmailService emailService;

    /** 교사 회원가입 (항상 승인 대기 상태로 생성). */
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody SignupRequest signup) {
        JoinTeacherRequest request = signup.toJoinRequest();

        if (teacherService.findDuplicateTeacherID(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 가입된 아이디입니다."));
        }
        if (teacherService.findDuplicateTeacherEmail(request)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 가입된 이메일입니다."));
        }

        teacherService.joinTeacher(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "가입되었습니다. 관리자 승인 후 로그인할 수 있습니다."));
    }

    /** 아이디 찾기 — 이메일로 등록된 아이디를 발송. */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdRequest request) {
        Optional<FindIdResponse> idByEmail = teacherCertService.findIdByEmail(request);
        if (idByEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "등록된 이메일이 없습니다."));
        }
        emailService.sendUserId(idByEmail.get());
        return ResponseEntity.ok(Map.of("message", "이메일로 아이디를 전송했습니다."));
    }

    /** 비밀번호 찾기 1단계 — 아이디+이메일 확인 후 인증번호 발송. */
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@Valid @RequestBody FindPasswordRequest request) {
        if (!teacherCertService.emailConfirmation(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "등록된 이메일이 없습니다."));
        }
        if (!teacherCertService.idConfirmation(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "등록된 아이디가 없습니다."));
        }
        String authCode = authCodeService.issue(request.username());
        emailService.sendAuthCode(request.email(), authCode);
        return ResponseEntity.ok(Map.of("message", "인증번호를 이메일로 전송했습니다."));
    }

    /** 비밀번호 찾기 2단계 — 인증번호 검증. */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody CertRequest request) {
        AuthVerification result = authCodeService.verify(request.username(), request.authNum());
        if (result == AuthVerification.SUCCESS) {
            return ResponseEntity.ok(Map.of("message", "인증되었습니다."));
        }
        String message = switch (result) {
            case NOT_ISSUED -> "인증번호를 먼저 전송해 주세요.";
            case EXPIRED -> "인증시간이 만료되었습니다.";
            default -> "인증번호가 일치하지 않습니다.";
        };
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }

    /** 비밀번호 찾기 3단계 — 새 비밀번호로 초기화. */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PwdEditRequest request) {
        teacherCertService.initializePassword(request);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }
}
