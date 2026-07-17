package com.attendance.scheduler.infra.config;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로컬 개발/검증 전용 시드 데이터. (local 프로파일에서만 동작)
 * 교사 1명(teacher1 / teacher123! · 승인됨) + 학생 2명 + 수업 1건.
 */
@Slf4j
@Component
@Profile("local")
@Order(10)
@RequiredArgsConstructor
public class LocalDataInitializer implements ApplicationRunner {

    private final TeacherJpaRepository teacherJpaRepository;
    private final StudentJpaRepository studentJpaRepository;
    private final ClassJpaRepository classJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (teacherJpaRepository.existsByUsername("teacher1")) {
            return;
        }

        Teacher teacher = teacherJpaRepository.save(Teacher.builder()
                .username("teacher1")
                .teacherName("김선생")
                .password(passwordEncoder.encode("teacher123!"))
                .email("teacher1@example.com")
                .approved(true)
                .build());

        // 담당교사 변경/계정삭제 테스트용: 학생이 없는 두 번째 교사
        teacherJpaRepository.save(Teacher.builder()
                .username("teacher2")
                .teacherName("박선생")
                .password(passwordEncoder.encode("teacher123!"))
                .email("teacher2@example.com")
                .approved(true)
                .build());

        Student minjun = newStudent("김민준", "01011112222", "서울시 강남구", "101동 1001호", "01099998888", teacher);
        Student seoyeon = newStudent("이서연", "01033334444", "서울시 서초구", "202동 2002호", "01077776666", teacher);
        studentJpaRepository.save(minjun);
        studentJpaRepository.save(seoyeon);

        Course course = Course.builder()
                .monday(1).tuesday(0).wednesday(3).thursday(0).friday(5)
                .build();
        course.setStudentEntity(minjun);
        course.setTeacherEntity(teacher);
        classJpaRepository.save(course);

        log.info("[local] 시드 데이터 생성 완료 (teacher1 / teacher123!)");
    }

    private Student newStudent(String name, String phone, String address, String detail, String parentPhone, Teacher teacher) {
        Student student = Student.builder()
                .studentName(name)
                .studentPhoneNumber(phone)
                .studentAddress(address)
                .studentDetailedAddress(detail)
                .studentParentPhoneNumber(parentPhone)
                .build();
        student.setTeacherEntity(teacher);
        return student;
    }
}
