package com.attendance.scheduler.testDataSet;

import com.attendance.scheduler.infra.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SecurityConfig 의 인가 규칙을 검증한다.
 * 인가는 컨트롤러/뷰 렌더링 이전에 필터 체인에서 평가되므로,
 * 도메인 상태와 무관하게 안정적으로 검증된다.
 */
@IntegrationTest
class TeacherSecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void loginForm_isPublic_andRendersLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("login"));
    }

    @Test
    void protectedPath_whenUnauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/manage/class"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    // 데이터 조회 없이 폼 뷰만 반환하는 admin 전용 경로를 사용해
    // 도메인 데이터 상태와 무관하게 인가 결정만 검증한다.
    @Test
    void adminPath_whenTeacher_isForbidden() throws Exception {
        mockMvc.perform(get("/admin/help/password").with(user("teacher").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPath_whenAdmin_isGranted() throws Exception {
        mockMvc.perform(get("/admin/help/password").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }
}
