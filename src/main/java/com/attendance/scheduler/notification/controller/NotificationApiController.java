package com.attendance.scheduler.notification.controller;

import com.attendance.scheduler.notification.application.NotificationService;
import com.attendance.scheduler.notification.dto.NotificationResponse;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 알림(notification) 도메인 REST API — 로그인한 교사 본인의 알림.
 * 수업 신청 시 CourseEventListener 가 생성한 알림을 조회/읽음 처리한다.
 * (관리자는 담당 교사가 아니므로 알림이 없다 → 빈 목록)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationApiController {

    private final NotificationService notificationService;
    private final TeacherJpaRepository teacherJpaRepository;

    /** 현재 교사의 알림 목록(최신순). */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> list(Authentication authentication) {
        Teacher teacher = teacherJpaRepository.findByUsernameIs(authentication.getName());
        if (teacher == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(notificationService.findByTeacherEntityOrderByCreatedDesc(teacher));
    }

    /** 알림 하나 읽음 처리. */
    @PostMapping("/{id}/read")
    public ResponseEntity<?> read(@PathVariable Long id, Authentication authentication) {
        Teacher teacher = teacherJpaRepository.findByUsernameIs(authentication.getName());
        if (teacher == null) {
            return ResponseEntity.ok(Map.of("message", "대상이 없습니다."));
        }
        notificationService.CheckedByTeacherEntity(teacher, id);
        return ResponseEntity.ok(Map.of("message", "읽음 처리되었습니다."));
    }

    /** 전체 읽음 처리. */
    @PostMapping("/read-all")
    public ResponseEntity<?> readAll(Authentication authentication) {
        Teacher teacher = teacherJpaRepository.findByUsernameIs(authentication.getName());
        if (teacher != null) {
            notificationService.markAllAsRead(teacher);
        }
        return ResponseEntity.ok(Map.of("message", "모두 읽음 처리되었습니다."));
    }
}
