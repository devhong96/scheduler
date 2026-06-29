package com.attendance.scheduler.admin.controller;

import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.admin.dto.ChangeTeacherRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api")
public class AdminApiController {

    public final AdminService adminService;

    @PostMapping("/grant/{teacherId}")
    public ResponseEntity<String> grantAuth(@PathVariable String teacherId) {
        adminService.grantAuth(teacherId);
        return ResponseEntity.ok("승인되었습니다.");
    }

    @PostMapping("/revoke/{teacherId}")
    public ResponseEntity<String> revokeAuth(@PathVariable String teacherId) {
        adminService.revokeAuth(teacherId);
        return ResponseEntity.ok("승인 취소되었습니다.");
    }

    @PostMapping("/changeTeacher")
    public ResponseEntity<String> changeTeacher(ChangeTeacherRequest changeTeacherDTO) {
        try {
            adminService.changeExistTeacher(changeTeacherDTO);
            return ResponseEntity.ok("변경되었습니다.");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/delete/{teacherId}")
    public ResponseEntity<String> delete(@PathVariable String teacherId) {
        adminService.deleteTeacherAccount(teacherId);
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
