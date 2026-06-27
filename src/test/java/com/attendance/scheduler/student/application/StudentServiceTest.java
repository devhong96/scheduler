package com.attendance.scheduler.student.application;

import com.attendance.scheduler.student.domain.StudentEntity;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.application.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testStudentInformationDTO;
import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class StudentServiceTest {

    @Autowired private StudentJpaRepository studentJpaRepository;
    @Autowired private TeacherService teacherService;

    @BeforeEach
    void beforeEach(){
        boolean duplicateTeacherID = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        if (!duplicateTeacherID) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }

    @Test
    @DisplayName("학생 인적 사항 정보 저장")
    void findStudentEntityByStudentName() {
        Optional<StudentEntity> studentEntityByStudentName
                = Optional.ofNullable(studentJpaRepository.findStudentEntityByStudentName(testStudentInformationDTO().getStudentName()));
        studentEntityByStudentName.ifPresent( studentEntity ->
                assertThat(testStudentInformationDTO().getStudentName()).isEqualTo(studentEntity.getStudentName()));
    }

}
