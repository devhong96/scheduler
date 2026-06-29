package com.attendance.scheduler.testDataSet;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.StudentInformationResponse;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import com.attendance.scheduler.teacher.dto.RegisterStudentRequest;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.test.annotation.Rollback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SampleDataTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentJpaRepository studentJpaRepository;

    @Autowired
    private TeacherJpaRepository teacherJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JoinTeacherRequest sampleTeacherDataSet(){
        return new JoinTeacherRequest("sampleTeacher", "123", "김교사", "sampleTeacherDataSet@gmail.com", true);
    }

    public JoinTeacherRequest sample2TeacherDataSet(){
        return new JoinTeacherRequest("sample2Teacher", "123", "박교사", "sample2TeacherDataSet@gmail.com", true);
    }

    public static StudentInformationResponse sampleStudentInformationDTO() {
        return new StudentInformationResponse(0L, "김샘플", "010-1234-1234", "대한민국 저기 어디", "어디", "010-1234-1233", "", LocalDateTime.now());
    }

    public ClassRequest sampleClass(){
        return new ClassRequest("sampleStudent", 1, 2, 3, 4, 5);
    }

        @Test
        @DisplayName("샘플 교사 정보")
        void saveSampleTeacherDataSet(){
            String encode = passwordEncoder.encode(sampleTeacherDataSet().password());
            teacherService.joinTeacher(sampleTeacherDataSet().withPassword(encode));

            String encode2 = passwordEncoder.encode(sample2TeacherDataSet().password());
            teacherService.joinTeacher(sample2TeacherDataSet().withPassword(encode2));
        }

    @Test
    @DisplayName("샘플 학생 정보")
    @Transactional
    void addStudentDataSet(){

        // 다른 테스트에 의존하지 않도록 교사를 자체 생성
        Teacher testTeacher = ensureSampleTeacher();

        for (int i = 0; i < 4; i++) {
            RegisterStudentRequest registerStudentDTO = new RegisterStudentRequest("김샘플" + i,
                    "01012341234", "대한민국 저기 어디", "어디", "01012341233",
                    testTeacher.getTeacherName(), testTeacher.getUsername());
            Student entity = registerStudentDTO.toEntity();
            entity.setTeacherEntity(testTeacher);
            studentJpaRepository.save(entity);
        }

        assertNotNull(studentJpaRepository.findStudentEntityByStudentName("김샘플0"));
    }


        @Test
        @DisplayName("샘플 학생 수강 정보")
        @Transactional
        void saveSampleStudent() throws InterruptedException {
            // 교사와 학생을 자체 생성한 뒤 수강 정보 저장
            Teacher sampleTeacher = ensureSampleTeacher();
            RegisterStudentRequest student = new RegisterStudentRequest("sampleStudent",
                    "01012341234", "대한민국 저기 어디", "어디", "01012341233",
                    sampleTeacher.getTeacherName(), sampleTeacher.getUsername());
            teacherService.registerStudentInformation(student);

            classService.saveClassTable(sampleClass());

            assertTrue(classService.findStudentClasses("sampleStudent").isPresent());
        }

    // sampleTeacher가 없으면 생성하고 엔티티를 반환한다.
    private Teacher ensureSampleTeacher() {
        JoinTeacherRequest teacher = sampleTeacherDataSet();
        if (!teacherService.findDuplicateTeacherID(teacher)) {
            teacherService.joinTeacher(
                    teacher.withPassword(passwordEncoder.encode(teacher.password())));
        }
        return teacherJpaRepository.findByUsernameIs(teacher.username());
    }


        @DisplayName("샘플 데이터 삭제")
        void deleteSampleData(){

        }
    }
