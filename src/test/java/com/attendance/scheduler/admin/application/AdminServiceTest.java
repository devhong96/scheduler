package com.attendance.scheduler.admin.application;

import com.attendance.scheduler.admin.domain.AdminEntity;
import com.attendance.scheduler.admin.dto.ChangeTeacherDTO;
import com.attendance.scheduler.admin.dto.EditEmailDTO;
import com.attendance.scheduler.admin.dto.EmailDTO;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.domain.TeacherEntity;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired private AdminService adminService;
    @Autowired private AdminJpaRepository adminJpaRepository;
    @Autowired private TeacherService teacherService;
    @Autowired private AdminCertService adminCertService;
    @Autowired private TeacherJpaRepository teacherJpaRepository;

    @BeforeEach
    void joinSampleTeacherAccount(){
        Optional<TeacherEntity> existingTeacher = Optional
                .ofNullable(teacherJpaRepository
                        .findByUsernameIs(testTeacherDataSet().getUsername()));
        if (existingTeacher.isEmpty()) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }


    @Test
    void findAdminEmailByID() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUsername("admin");
        AdminEntity adminAccount = adminJpaRepository
                .findByUsernameIs(emailDTO.getUsername());

        EmailDTO build = EmailDTO.builder()
                .username(adminAccount.getUsername())
                .email(adminAccount.getEmail())
                .build();

        assertThat("admin").isEqualTo(build.getUsername());
        assertThat("adminTest@gmail.com").isEqualTo( build.getEmail());
    }

//    @Test
    @DisplayName("교사에게 권한 부여")
    void grantAuth() {

        //Given

        //When
//        adminService.grantAuth(approveTeacherDTO);

        //Then
        TeacherEntity teacherEntity = teacherJpaRepository
                .findByUsernameIs(testTeacherDataSet().getUsername());
        assertTrue(teacherEntity.isApproved());
    }



    @Test
    @DisplayName("교사에게서 권한 회수")
    void revokeAuth() {

        //When
        adminService.revokeAuth(testTeacherDataSet().getUsername());

        //Then
        TeacherEntity teacherEntity = teacherJpaRepository
                .findByUsernameIs(testTeacherDataSet().getUsername());
        assertFalse(teacherEntity.isApproved());
    }



    @Test//TODO
    void changeExistTeacher() {

        ChangeTeacherDTO changeTeacherDTO = new ChangeTeacherDTO();
        changeTeacherDTO.setTeacherId(2L);
        changeTeacherDTO.setStudentId(2L);

        adminService.changeExistTeacher(changeTeacherDTO);


    }



    @Test
    @DisplayName("교사 계정 삭제")
    void deleteTeacher() {
        teacherJpaRepository.deleteByUsernameIs(testTeacherDataSet().getUsername());
        boolean duplicateTeacherId = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        assertFalse(duplicateTeacherId);
    }

    @Test
    @DisplayName("이메일 변경")
    void updateEmail(){

        //Given
        EditEmailDTO editEmailDTO = new EditEmailDTO();
        editEmailDTO.setUsername("admin");
        editEmailDTO.setEmail("adminTest@gmail.com");

        //When
        adminCertService.updateEmail(editEmailDTO);
        AdminEntity byUsernameIs = adminJpaRepository
                .findByUsernameIs(editEmailDTO.getUsername());

        //Then
        assertThat(editEmailDTO.getEmail()).isEqualTo(byUsernameIs.getEmail());
    }
}

