package com.attendance.scheduler.comment.controller;

import com.attendance.scheduler.comment.application.CommentService;
import com.attendance.scheduler.comment.dto.CommentRequest;
import com.attendance.scheduler.student.application.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 댓글(comment) 도메인 REST API — 공개.
 * 학생 본인 인증(이름 + 학부모 전화번호=password)으로 작성/삭제한다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {

    private final CommentService commentService;
    private final StudentService studentService;

    /** 댓글 작성. */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody CommentRequest request) {
        if (!studentService.existStudentEntityByStudentNameAndStudentParentPhoneNumber(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "허가되지 않은 사용자 입니다."));
        }
        commentService.saveComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "등록되었습니다."));
    }

    /** 댓글 삭제. */
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody CommentRequest request) {
        if (!studentService.existStudentEntityByStudentNameAndStudentParentPhoneNumber(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "권한이 없습니다."));
        }
        commentService.deleteComment(request);
        return ResponseEntity.ok(Map.of("message", "삭제되었습니다."));
    }
}
