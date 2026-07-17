package com.attendance.scheduler.admin.controller;

import com.attendance.scheduler.infra.ApiSupport;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 관리자 REST API 및 인가 검증.
 */
class AdminRestControllerTest extends ApiSupport {

    @Autowired
    TeacherService teacherService;

    @BeforeEach
    void setUp() {
        teacherService.joinTeacher(new JoinTeacherRequest("teacher1", "pw123!", "김교사", "t1@example.com", false));
    }

    @Test
    void teachers_asAdmin_returnsList() throws Exception {
        mockMvc.perform(get("/api/admin/teachers").header("Authorization", bearer("admin", "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("teacher1"));
    }

    @Test
    void teachers_asTeacher_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/teachers").header("Authorization", bearer("teacher1", "ROLE_TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teachers_noToken_unauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/teachers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void grant_thenTeacherIsApproved() throws Exception {
        mockMvc.perform(post("/api/admin/teachers/teacher1/grant").header("Authorization", bearer("admin", "ROLE_ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/teachers").header("Authorization", bearer("admin", "ROLE_ADMIN")))
                .andExpect(jsonPath("$[0].approved").value(true));
    }
}
