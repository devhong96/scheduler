package com.attendance.scheduler.common.controller;

import com.attendance.scheduler.infra.ApiSupport;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JWT 로그인 REST 검증.
 */
class AuthApiControllerTest extends ApiSupport {

    @Autowired
    TeacherService teacherService;

    @BeforeEach
    void setUp() {
        teacherService.joinTeacher(new JoinTeacherRequest("teacher1", "pw123!", "김교사", "t1@example.com", true));
    }

    @Test
    void login_success_returnsAccessToken() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"teacher1\",\"password\":\"pw123!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.roles[0]").value("ROLE_TEACHER"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"teacher1\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_unapprovedTeacher_returns401() throws Exception {
        teacherService.joinTeacher(new JoinTeacherRequest("pending", "pw123!", "대기", "pending@example.com", false));
        mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"pending\",\"password\":\"pw123!\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_withToken_returnsUsernameAndRoles() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/auth/me")
                        .header("Authorization", bearer("teacher1", "ROLE_TEACHER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("teacher1"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_TEACHER"));
    }
}
