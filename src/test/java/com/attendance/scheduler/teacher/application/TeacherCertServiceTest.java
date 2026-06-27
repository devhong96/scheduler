package com.attendance.scheduler.teacher.application;


import com.attendance.scheduler.common.dto.LoginDTO;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.EmailDTO;
import com.attendance.scheduler.teacher.dto.PwdEditDTO;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TeacherCertServiceTest {

    @Autowired private TeacherService teacherService;
    @Autowired private TeacherCertService teacherCertService;
    @Autowired private TeacherJpaRepository teacherJpaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserDetailsService userDetailsService;

    @BeforeEach
    @DisplayName("회원가입")
    public void joinTeacherDTO() {
        Optional<Teacher> existingTeacher = Optional
                .ofNullable(teacherJpaRepository
                        .findByUsernameIs(testTeacherDataSet().getUsername()));

        if (existingTeacher.isEmpty()) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }

    @Test
    @DisplayName("교사 로그인")
    void loginTeacher() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(testTeacherDataSet().getUsername());
        loginDTO.setPassword(testTeacherDataSet().getPassword());

        //when
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDTO.getUsername());

        //then
        boolean matches = passwordEncoder
                .matches(loginDTO.getPassword(), userDetails.getPassword());
        assertEquals(testTeacherDataSet().getUsername(), userDetails.getUsername());
        assertTrue(matches);
    }

    @Test
    @DisplayName("아이디 찾을 때, 이메일 검증")
    void findIdByEmail() {
        boolean duplicateTeacherEmail = teacherService
                .findDuplicateTeacherEmail(testTeacherDataSet());
        assertTrue(duplicateTeacherEmail);
    }

    @Test
    @DisplayName("ID 중복 확인")
    void idConfirmation(){
        boolean existedByUsername = teacherJpaRepository
                .existsByUsername(testTeacherDataSet().getUsername());
        assertTrue(existedByUsername);
    }

    @Test
    @DisplayName("Email 중복 확인")
    void emailConfirmation(){
        boolean existedByUsername = teacherJpaRepository
                .existsByEmail(testTeacherDataSet().getEmail());
        assertTrue(existedByUsername);
    }

    @Test
    @DisplayName("아이디로 이메일 정보 찾기")
    void findTeacherEmailByID() {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setUsername(testTeacherDataSet().getUsername());

        Teacher teacherEntity = teacherJpaRepository
                .findByUsernameIs(emailDTO.getUsername());

        EmailDTO build = EmailDTO.builder()
                .username(teacherEntity.getUsername())
                .email(teacherEntity.getEmail())
                .build();

        List<EmailDTO> emailDTOS = Collections.singletonList(build);

        assertEquals(testTeacherDataSet().getUsername(), emailDTOS.get(0).getUsername());
        assertEquals(testTeacherDataSet().getEmail(), emailDTOS.get(0).getEmail());

    }

    @Test
    @DisplayName("비밀번호 변경 참 거짓 확인")
    void pwdEdit() {

        //비밀번호 변경
        PwdEditDTO pwdEditDTO = new PwdEditDTO();
        pwdEditDTO.setUsername(testTeacherDataSet().getUsername());
        pwdEditDTO.setPassword("root123!@#");

            //Given, 교사 로그인
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername(testTeacherDataSet().getUsername());

            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(loginDTO.getUsername());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(testTeacherDataSet().getUsername(),
                            testTeacherDataSet().getPassword() , userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

        //when
        teacherCertService.initializePassword(pwdEditDTO);
        Teacher byUsernameIs = teacherJpaRepository
                .findByUsernameIs(pwdEditDTO.getUsername());

        //then
        //비밀번호 검증
        boolean pwdMatch = passwordEncoder
                .matches("root123!@#", byUsernameIs.getPassword());
        assertTrue(pwdMatch);
    }
}