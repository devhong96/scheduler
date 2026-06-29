package com.attendance.scheduler.common;

import com.attendance.scheduler.infra.IntegrationTest;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class LoginControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired TeacherService teacherService;


    @BeforeEach
    void beforeEach() {
        JoinTeacherRequest joinTeacherDTO = new JoinTeacherRequest("teacher", "123", "", "teacher@gmail.com", true);
        teacherService.joinTeacher(joinTeacherDTO);
    }

    @Test
    void teacherLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().is2xxSuccessful());

    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    void teacherLogin() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "teacher")
                        .param("password", "123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manage/class"))
                .andExpect(authenticated().withUsername("teacher"));
    }

}
