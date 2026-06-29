package com.attendance.scheduler.teacher.application;
import org.springframework.test.context.ActiveProfiles;


import com.attendance.scheduler.common.dto.LoginRequest;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.EmailResponse;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
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

@ActiveProfiles("test")
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
                        .findByUsernameIs(testTeacherDataSet().username()));

        if (existingTeacher.isEmpty()) {
            teacherService.joinTeacher(testTeacherDataSet());
        }
    }

    @Test
    @DisplayName("교사 로그인")
    void loginTeacher() {
        LoginRequest loginDTO = new LoginRequest(testTeacherDataSet().username(), testTeacherDataSet().password());

        //when
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDTO.username());

        //then
        boolean matches = passwordEncoder
                .matches(loginDTO.password(), userDetails.getPassword());
        assertEquals(testTeacherDataSet().username(), userDetails.getUsername());
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
                .existsByUsername(testTeacherDataSet().username());
        assertTrue(existedByUsername);
    }

    @Test
    @DisplayName("Email 중복 확인")
    void emailConfirmation(){
        boolean existedByUsername = teacherJpaRepository
                .existsByEmail(testTeacherDataSet().email());
        assertTrue(existedByUsername);
    }

    @Test
    @DisplayName("아이디로 이메일 정보 찾기")
    void findTeacherEmailByID() {
        EmailResponse emailDTO = new EmailResponse(testTeacherDataSet().username(), "");

        Teacher teacherEntity = teacherJpaRepository
                .findByUsernameIs(emailDTO.username());

        EmailResponse build = new EmailResponse(teacherEntity.getUsername(), teacherEntity.getEmail());

        List<EmailResponse> emailDTOS = Collections.singletonList(build);

        assertEquals(testTeacherDataSet().username(), emailDTOS.get(0).username());
        assertEquals(testTeacherDataSet().email(), emailDTOS.get(0).email());

    }

    @Test
    @DisplayName("비밀번호 변경 참 거짓 확인")
    void pwdEdit() {

        //비밀번호 변경
        PwdEditRequest pwdEditDTO = new PwdEditRequest(testTeacherDataSet().username(), "root123!@#");

            //Given, 교사 로그인
            LoginRequest loginDTO = new LoginRequest(testTeacherDataSet().username(), testTeacherDataSet().password());

            UserDetails userDetails = userDetailsService
                    .loadUserByUsername(loginDTO.username());

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(testTeacherDataSet().username(),
                            testTeacherDataSet().password() , userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

        //when
        teacherCertService.initializePassword(pwdEditDTO);
        Teacher byUsernameIs = teacherJpaRepository
                .findByUsernameIs(pwdEditDTO.username());

        //then
        //비밀번호 검증
        boolean pwdMatch = passwordEncoder
                .matches("root123!@#", byUsernameIs.getPassword());
        assertTrue(pwdMatch);
    }
}
