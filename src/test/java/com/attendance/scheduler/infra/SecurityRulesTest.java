package com.attendance.scheduler.infra;

import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 단일 stateless JWT 체인의 인가 규칙 검증.
 * 인가는 컨트롤러 실행 이전 필터 단계에서 결정되므로 도메인 데이터와 무관하게 안정적이다.
 */
class SecurityRulesTest extends ApiSupport {

    @Test
    void publicBoardList_isAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/api/board"))
                .andExpect(status().isOk());
    }

    @Test
    void manage_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/manage/classes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminApi_withTeacherRole_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/teachers").header("Authorization", bearer("t", "ROLE_TEACHER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminApi_withAdminRole_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/teachers").header("Authorization", bearer("admin", "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void boardWrite_withoutAdmin_isRejected() throws Exception {
        // 무토큰 → 401
        mockMvc.perform(post("/api/board").contentType(APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"content\":\"c\"}"))
                .andExpect(status().isUnauthorized());

        // 교사 토큰 → 403 (관리자만 작성 가능)
        mockMvc.perform(post("/api/board").header("Authorization", bearer("t", "ROLE_TEACHER"))
                        .contentType(APPLICATION_JSON)
                        .content("{\"title\":\"t\",\"content\":\"c\"}"))
                .andExpect(status().isForbidden());
    }
}
