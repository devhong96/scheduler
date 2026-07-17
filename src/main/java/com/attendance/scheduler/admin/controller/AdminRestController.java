package com.attendance.scheduler.admin.controller;

import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.admin.dto.ChangeTeacherRequest;
import com.attendance.scheduler.teacher.dto.TeacherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 관리자(admin) 도메인 REST API (/api/admin) — 전부 ROLE_ADMIN 필요(SecurityConfig).
 * 교사 목록 조회, 승인/승인취소, 담당교사 변경, 계정 삭제.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminRestController {

    private final AdminService adminService;

    /** 교사 목록. */
    @GetMapping("/teachers")
    public ResponseEntity<List<TeacherResponse>> teachers() {
        return ResponseEntity.ok(adminService.getTeacherList());
    }

    /** 교사 승인. */
    @PostMapping("/teachers/{teacherId}/grant")
    public ResponseEntity<?> grant(@PathVariable String teacherId) {
        adminService.grantAuth(teacherId);
        return ResponseEntity.ok(Map.of("message", "승인되었습니다."));
    }

    /** 교사 승인 취소. */
    @PostMapping("/teachers/{teacherId}/revoke")
    public ResponseEntity<?> revoke(@PathVariable String teacherId) {
        adminService.revokeAuth(teacherId);
        return ResponseEntity.ok(Map.of("message", "승인이 취소되었습니다."));
    }

    /** 담당교사 변경 (교시 충돌 시 IllegalStateException → 400). */
    @PostMapping("/change-teacher")
    public ResponseEntity<?> changeTeacher(@RequestBody ChangeTeacherRequest request) {
        adminService.changeExistTeacher(request);
        return ResponseEntity.ok(Map.of("message", "변경되었습니다."));
    }

    /** 교사 계정 삭제 (담당 수업이 남아 있으면 IllegalStateException → 400). */
    @DeleteMapping("/teachers/{teacherId}")
    public ResponseEntity<?> delete(@PathVariable String teacherId) {
        adminService.deleteTeacherAccount(teacherId);
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
