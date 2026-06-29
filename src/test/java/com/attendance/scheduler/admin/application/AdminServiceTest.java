package com.attendance.scheduler.admin.application;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.dto.ChangeTeacherRequest;
import com.attendance.scheduler.admin.dto.EditEmailRequest;
import com.attendance.scheduler.admin.dto.EmailResponse;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testStudentInformationDTO;
import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired private AdminService adminService;
    @Autowired private AdminJpaRepository adminJpaRepository;
    @Autowired private TeacherService teacherService;
    @Autowired private AdminCertService adminCertService;
    @Autowired private TeacherJpaRepository teacherJpaRepository;
    @Autowired private StudentJpaRepository studentJpaRepository;

    @BeforeEach
    void joinSampleTeacherAccount(){
        Optional<Teacher> existingTeacher = Optional
                .ofNullable(teacherJpaRepository
                        .findByUsernameIs(testTeacherDataSet().username()));
        if (existingTeacher.isEmpty()) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }


    @Test
    void findAdminEmailByID() {
        EmailResponse emailDTO = new EmailResponse("admin", "");
        Admin adminAccount = adminJpaRepository
                .findByUsernameIs(emailDTO.username());

        EmailResponse build = new EmailResponse(adminAccount.getUsername(), adminAccount.getEmail());

        assertThat("admin").isEqualTo(build.username());
        assertThat("adminTest@gmail.com").isEqualTo(build.email());
    }

//    @Test
    @DisplayName("교사에게 권한 부여")
    void grantAuth() {

        //Given

        //When
//        adminService.grantAuth(approveTeacherDTO);

        //Then
        Teacher teacherEntity = teacherJpaRepository
                .findByUsernameIs(testTeacherDataSet().username());
        assertTrue(teacherEntity.isApproved());
    }



    @Test
    @DisplayName("교사에게서 권한 회수")
    void revokeAuth() {

        //When
        adminService.revokeAuth(testTeacherDataSet().username());

        //Then
        Teacher teacherEntity = teacherJpaRepository
                .findByUsernameIs(testTeacherDataSet().username());
        assertFalse(teacherEntity.isApproved());
    }



    @Test
    @DisplayName("학생을 다른 교사에게 재배정")
    void changeExistTeacher() {

        //Given: 두 번째 교사와, 첫 교사(testTeacher)에 소속된 학생을 직접 생성
        JoinTeacherRequest targetTeacher =
                new JoinTeacherRequest("targetTeacher", "123", "박교사", "targetTeacher@gmail.com", true);
        if (!teacherService.findDuplicateTeacherID(targetTeacher)) {
            teacherService.joinTeacher(targetTeacher);
        }
        teacherService.registerStudentInformation(testStudentInformationDTO());

        Long targetTeacherId = teacherJpaRepository
                .findByUsernameIs(targetTeacher.username()).getId();
        Long studentId = studentJpaRepository
                .findStudentEntityByStudentName(testStudentInformationDTO().studentName()).getId();

        //When: 학생을 두 번째 교사에게 재배정 (대상 교사는 수업이 없어 충돌 없음)
        adminService.changeExistTeacher(new ChangeTeacherRequest(targetTeacherId, studentId));

        //Then
        Student movedStudent = studentJpaRepository.findStudentEntityById(studentId);
        assertThat(movedStudent.getTeacherEntity().getUsername())
                .isEqualTo(targetTeacher.username());
    }



    @Test
    @DisplayName("교사 계정 삭제")
    void deleteTeacher() {
        teacherJpaRepository.deleteByUsernameIs(testTeacherDataSet().username());
        boolean duplicateTeacherId = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        assertFalse(duplicateTeacherId);
    }

    @Test
    @DisplayName("이메일 변경")
    void updateEmail(){

        //Given
        EditEmailRequest editEmailDTO = new EditEmailRequest("admin", "adminTest@gmail.com");

        //When
        adminCertService.updateEmail(editEmailDTO);
        Admin byUsernameIs = adminJpaRepository
                .findByUsernameIs(editEmailDTO.username());

        //Then
        assertThat(editEmailDTO.email()).isEqualTo(byUsernameIs.getEmail());
    }
}
