package com.attendance.scheduler.Service.Impl;

import com.attendance.scheduler.Dto.Admin.ApproveTeacherDTO;
import com.attendance.scheduler.Dto.EditEmailDTO;
import com.attendance.scheduler.Dto.EmailDTO;
import com.attendance.scheduler.Entity.AdminEntity;
import com.attendance.scheduler.Entity.TeacherEntity;
import com.attendance.scheduler.Repository.jpa.AdminRepository;
import com.attendance.scheduler.Repository.jpa.TeacherRepository;
import com.attendance.scheduler.Service.AdminService;
import com.attendance.scheduler.Service.TeacherService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.attendance.scheduler.Config.TestDataSet.testTeacherDataSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Transactional
class AdminServiceImplTest {

    @Autowired public AdminService adminService;
    @Autowired public AdminRepository adminRepository;
    @Autowired private TeacherService teacherService;
    @Autowired private TeacherRepository teacherRepository;

    private ApproveTeacherDTO approveTeacherDTO;

    @BeforeEach
    void joinSampleTeacherAccount(){
        Optional<TeacherEntity> existingTeacher = Optional
                .ofNullable(teacherRepository
                        .findByUsernameIs(testTeacherDataSet().getUsername()));
        if (existingTeacher.isEmpty()) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }


    @Test
    void findAdminEmailByID() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUsername("admin");
        AdminEntity adminAccount = adminRepository
                .findByUsernameIs(emailDTO.getUsername());

        EmailDTO build = EmailDTO.builder()
                .username(adminAccount.getUsername())
                .email(adminAccount.getEmail())
                .build();

        assertThat("admin").isEqualTo(build.getUsername());
        assertThat("adminTest@gmail.com").isEqualTo( build.getEmail());
    }

    @Test
    @DisplayName("교사에게 권한 부여")
    void grantAuth() {

        //Given
        approveTeacherDTO = new ApproveTeacherDTO();
        approveTeacherDTO.setUsername(testTeacherDataSet().getUsername());

        //When
        adminService.grantAuth(approveTeacherDTO);

        //Then
        TeacherEntity teacherEntity = teacherRepository
                .findByUsernameIs(testTeacherDataSet().getUsername());
        assertTrue(teacherEntity.isApproved());
    }

    @Test
    @DisplayName("교사에게서 권한 회수")
    void revokeAuth() {

        //Given
        approveTeacherDTO = new ApproveTeacherDTO();
        approveTeacherDTO.setUsername(testTeacherDataSet().getUsername());

        //When
        adminService.revokeAuth(approveTeacherDTO);

        //Then
        TeacherEntity teacherEntity = teacherRepository
                .findByUsernameIs(testTeacherDataSet().getUsername());
        assertFalse(teacherEntity.isApproved());
    }

    @Test
    @DisplayName("이메일 변경")
    void updateEmail(){

        //Given
        EditEmailDTO editEmailDTO = new EditEmailDTO();
        editEmailDTO.setUsername("admin");
        editEmailDTO.setEmail("adminTest@gmail.com");

        //When
        adminService.updateEmail(editEmailDTO);
        AdminEntity byUsernameIs = adminRepository
                .findByUsernameIs(editEmailDTO.getUsername());

        //Then
        assertThat(editEmailDTO.getEmail()).isEqualTo(byUsernameIs.getEmail());
    }
}