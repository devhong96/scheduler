package com.attendance.scheduler.teacher.controller;

import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassResponse;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.StudentInformationRequest;
import com.attendance.scheduler.student.dto.StudentInformationResponse;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.ManageStudentsResponse;
import com.attendance.scheduler.teacher.dto.RegisterStudentRequest;
import com.attendance.scheduler.teacher.dto.StudentSearchCondition;
import com.attendance.scheduler.teacher.dto.TeacherResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 수업관리(teacher) 도메인 REST API — 기존 세션 기반 TeacherController 를 JWT 기반으로 이관.
 * TEACHER 는 본인 수업/학생만, ADMIN 은 전체를 다룬다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manage")
public class TeacherApiController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final AdminService adminService;

    /** 수업 시간표 목록. 교사는 본인 담당 학생만 필터. */
    @GetMapping("/classes")
    public ResponseEntity<List<ClassResponse>> classes(Authentication authentication) {
        List<ClassResponse> classTable = classService.findStudentClassList();

        if (isTeacher(authentication)) {
            List<TeacherResponse> info = adminService.findTeacherInformation(authentication.getName());
            String teacherName = info.get(0).teacherName();
            List<ClassResponse> filtered = classTable.stream()
                    .filter(c -> c.teacherName().equals(teacherName))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(filtered);
        }
        return ResponseEntity.ok(classTable);
    }

    /** 수업(시간표) 삭제. */
    @DeleteMapping("/classes/{studentName}")
    public ResponseEntity<String> deleteClass(@PathVariable String studentName) {
        classService.deleteClass(studentName);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    /** 학생정보 목록(검색 + 페이징). 관리자는 교사 목록도 함께 반환. */
    @GetMapping("/students")
    public ResponseEntity<ManageStudentsResponse> students(
            @ModelAttribute StudentSearchCondition condition, Pageable pageable, Authentication authentication) {
        Page<StudentInformationResponse> page = teacherService.findStudentInformationList(condition, pageable);
        List<TeacherResponse> teachers = isAdmin(authentication) ? adminService.getTeacherList() : List.of();
        return ResponseEntity.ok(ManageStudentsResponse.of(page, teachers));
    }

    /** 학생정보 등록. teacherUsername 은 인증 principal 에서 채운다. */
    @PostMapping("/students")
    public ResponseEntity<?> registerStudent(
            @Valid @RequestBody RegisterStudentRequest request, Authentication authentication) {

        if (studentService.existStudentEntityByStudentName(request.studentName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "이미 등록된 학생의 이름입니다."));
        }

        // 전화번호 하이픈 제거(컬럼 길이 11 · 숫자만 저장) 후 담당교사 지정
        request = request
                .withStudentPhoneNumber(request.studentPhoneNumber())
                .withStudentParentPhoneNumber(request.studentParentPhoneNumber())
                .withTeacherUsername(authentication.getName());
        try {
            teacherService.registerStudentInformation(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "등록되었습니다."));
        } catch (Exception e) {
            log.warn("학생정보 등록 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "등록에 실패했습니다."));
        }
    }

    /** 학생정보 삭제. */
    @DeleteMapping("/students/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        teacherService.deleteStudentInformation(new StudentInformationRequest(id));
        return ResponseEntity.ok("삭제되었습니다.");
    }

    private boolean isAdmin(Authentication authentication) {
        return hasAuthority(authentication, "ROLE_ADMIN");
    }

    private boolean isTeacher(Authentication authentication) {
        return hasAuthority(authentication, "ROLE_TEACHER") && !isAdmin(authentication);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
