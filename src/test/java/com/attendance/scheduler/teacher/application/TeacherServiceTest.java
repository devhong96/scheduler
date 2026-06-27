package com.attendance.scheduler.teacher.application;

import com.attendance.scheduler.common.dto.LoginDTO;
import com.attendance.scheduler.student.domain.StudentEntity;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testStudentInformationDTO;
import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TeacherServiceTest {

    @Autowired private TeacherService teacherService;
    @Autowired private TeacherJpaRepository teacherJpaRepository;
    @Autowired private StudentJpaRepository studentJpaRepository;
    @Autowired private UserDetailsService userDetailsService;

    @Autowired EntityManager entityManager;


    @BeforeEach
    void joinTestTeacherAccount(){
        boolean duplicateTeacherID = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        if (!duplicateTeacherID) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }

    @Test
    @DisplayName("교사 회원가입 확인")
    void joinTeacher() {
        boolean duplicateTeacherId = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());
        assertTrue(duplicateTeacherId);
    }

    @Test
    @DisplayName("교사 계정 삭제")
    void deleteTeacher() {
        teacherJpaRepository.deleteByUsernameIs(testTeacherDataSet().getUsername());
        boolean duplicateTeacherId = teacherService.findDuplicateTeacherID(testTeacherDataSet());
        assertFalse(duplicateTeacherId);
    }

    @Test
    @DisplayName("교사가 학생 정보를 등록")
    void registerStudentInformation() {

        //Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(testTeacherDataSet().getUsername());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testTeacherDataSet().getUsername(), null , userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        StudentEntity studentEntityByStudentNameIs = studentJpaRepository
                .findStudentEntityByStudentName(testStudentInformationDTO().getStudentName());

        //When
        if(studentEntityByStudentNameIs == null){
            teacherService.registerStudentInformation(testStudentInformationDTO());
        }

        //Then
        if (studentEntityByStudentNameIs != null) {
            String studentName = studentEntityByStudentNameIs.getStudentName();
            assertThat(testStudentInformationDTO().getStudentName()).isEqualTo(studentName);
        }
    }

    @Test
    @Transactional
    @DisplayName("학생 인적 사항 저장")
    void saveStudentInformation() {

        //Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(testTeacherDataSet().getUsername());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDTO.getUsername());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testTeacherDataSet().getUsername(),
                        null , userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        //When
        teacherService.registerStudentInformation(testStudentInformationDTO());

        //Then
        assertThat(testStudentInformationDTO().getStudentName())
                .isEqualTo(testStudentInformationDTO().getStudentName());
    }
}