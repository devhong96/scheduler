package com.attendance.scheduler.course.ui;

import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.course.dto.StudentClassFormResponse;
import com.attendance.scheduler.course.dto.StudentClassRequest;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.ClassListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 수강신청(student) 도메인 REST API — 공개(인증 불필요).
 * 기존 세션 기반 ClassController 의 findClass/submit 흐름을 JSON 으로 이관.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/class")
public class ClassApiController {

    private static final List<Integer> PERIODS = List.of(1, 2, 3, 4, 5, 6);

    private final ClassService classService;
    private final StudentService studentService;

    /** 학생 이름으로 수강신청 폼 데이터 조회. */
    @PostMapping("/find")
    public ResponseEntity<?> find(@Valid @RequestBody StudentClassRequest request) {
        String studentName = request.studentName();

        if (!studentService.existStudentEntityByStudentName(studentName)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "등록 되지 않은 학생입니다."));
        }

        ClassListResponse taken = classService.findTeachersClasses(studentName);
        Optional<StudentClassResponse> currentOpt = classService.findStudentClasses(studentName);
        StudentClassResponse current = currentOpt.orElse(new StudentClassResponse(studentName, 0, 0, 0, 0, 0));

        // 본인이 이미 신청한 교시는 다시 선택할 수 있도록 '이미 찬 교시' 목록에서 제거
        if (currentOpt.isPresent()) {
            taken.mondayClassList().remove(current.monday());
            taken.tuesdayClassList().remove(current.tuesday());
            taken.wednesdayClassList().remove(current.wednesday());
            taken.thursdayClassList().remove(current.thursday());
            taken.fridayClassList().remove(current.friday());
        }

        return ResponseEntity.ok(new StudentClassFormResponse(
                studentName,
                currentOpt.isPresent(),
                PERIODS,
                current,
                taken.mondayClassList(),
                taken.tuesdayClassList(),
                taken.wednesdayClassList(),
                taken.thursdayClassList(),
                taken.fridayClassList()
        ));
    }

    /** 수강신청 제출(신규/변경). 교시 중복 시 ClassService 가 IllegalStateException → 400. */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody ClassRequest request) {
        classService.saveClassTable(request);
        return ResponseEntity.ok(Map.of("message", "신청이 완료되었습니다."));
    }
}
