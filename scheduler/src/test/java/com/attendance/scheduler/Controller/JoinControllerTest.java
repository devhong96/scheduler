package com.attendance.scheduler.Controller;

import com.attendance.scheduler.Dto.Teacher.JoinTeacherDTO;
import com.attendance.scheduler.Dto.Teacher.LoginTeacherDTO;
import com.attendance.scheduler.Dto.Teacher.TeacherDTO;
import com.attendance.scheduler.Service.JoinService;
import com.attendance.scheduler.Service.LoginService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor
class JoinControllerTest {

    private static final String duplicateErrorMessage = "중복된 아이디 입니다.";

    @Autowired
    private JoinService joinService;

    @Autowired
    private LoginService loginService;

    @Test
    @Transactional
    @DisplayName("교사 회원가입")
    void approved() {

        //Given
        /*
         * 회원가입
         */
        JoinTeacherDTO joinTeacherDTO = new JoinTeacherDTO();
        joinTeacherDTO.setTeacherId("teacher");
        joinTeacherDTO.setTeacherPassword("123");
        joinTeacherDTO.setTeacherEmail("ghdtpgh8913@gmail.com");
        joinTeacherDTO.setTeacherName("김교사");

        /*
            로그인
         */
        LoginTeacherDTO loginTeacherDTO = new LoginTeacherDTO();
        loginTeacherDTO.setTeacherId("teacher");
        loginTeacherDTO.setTeacherPassword("123");


        //When
        joinService.joinTeacher(joinTeacherDTO);
        JoinTeacherDTO duplicateTeacherId = joinService.findDuplicateTeacherId(joinTeacherDTO);


        //Then
        if (duplicateTeacherId != null) {
            assertEquals("중복된 아이디 입니다.", duplicateErrorMessage);
        }

        TeacherDTO loginTeacher = loginService.loginTeacher(loginTeacherDTO);
        assertEquals("teacher", loginTeacher.getTeacherId());
    }
}