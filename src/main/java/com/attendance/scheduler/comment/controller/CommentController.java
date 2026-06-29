package com.attendance.scheduler.comment.controller;

import com.attendance.scheduler.comment.application.CommentService;
import com.attendance.scheduler.comment.dto.CommentRequest;
import com.attendance.scheduler.student.application.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/comment/")
@RequiredArgsConstructor
public class CommentController {

    public final CommentService commentService;
    public final StudentService studentService;

    @PostMapping("comment_submit")
    public ResponseEntity<String> submitComment(CommentRequest commentRequest) {
        boolean existed = studentService.existStudentEntityByStudentNameAndStudentParentPhoneNumber(commentRequest);
        if (!existed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("허가되지 않은 사용자 입니다.");
        }

        try {
            commentService.saveComment(commentRequest);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("comment_delete")
    public ResponseEntity<String> deleteComment(CommentRequest commentRequest) {
        boolean existed = studentService.existStudentEntityByStudentNameAndStudentParentPhoneNumber(commentRequest);
        if (!existed) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("권한이 없습니다.");
        }

        try {
            commentService.deleteComment(commentRequest);
            return ResponseEntity.ok("삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
