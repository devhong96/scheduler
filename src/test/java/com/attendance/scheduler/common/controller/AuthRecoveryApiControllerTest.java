package com.attendance.scheduler.common.controller;

import com.attendance.scheduler.infra.ApiSupport;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 회원가입 및 계정 복구(아이디/비밀번호 찾기) REST 검증.
 */
class AuthRecoveryApiControllerTest extends ApiSupport {

    @Autowired
    TeacherService teacherService;

    @Test
    void join_success_returns201() throws Exception {
        mockMvc.perform(post("/api/auth/join").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"newbie\",\"password\":\"pw123!\",\"teacherName\":\"신규\",\"email\":\"new@example.com\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void join_duplicateUsername_returns409() throws Exception {
        teacherService.joinTeacher(new JoinTeacherRequest("dup", "pw123!", "중복", "dup@example.com", false));

        mockMvc.perform(post("/api/auth/join").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"dup\",\"password\":\"pw123!\",\"teacherName\":\"중복2\",\"email\":\"other@example.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void join_blankFields_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/join").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"\",\"password\":\"\",\"teacherName\":\"\",\"email\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findId_existingEmail_returns200() throws Exception {
        teacherService.joinTeacher(new JoinTeacherRequest("finder", "pw123!", "찾기", "finder@example.com", true));

        mockMvc.perform(post("/api/auth/find-id").contentType(APPLICATION_JSON)
                        .content("{\"email\":\"finder@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void findId_unknownEmail_returns404() throws Exception {
        mockMvc.perform(post("/api/auth/find-id").contentType(APPLICATION_JSON)
                        .content("{\"email\":\"nobody@example.com\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findPassword_validIdAndEmail_returns200() throws Exception {
        teacherService.joinTeacher(new JoinTeacherRequest("pwuser", "pw123!", "비번", "pwuser@example.com", true));

        mockMvc.perform(post("/api/auth/find-password").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"pwuser\",\"email\":\"pwuser@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void verifyCode_wrongCode_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/verify-code").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"pwuser\",\"authNum\":\"000000\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
}
