package com.attendance.scheduler.infra;

import com.attendance.scheduler.infra.config.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

/**
 * REST(JWT) 통합 테스트 공통 베이스.
 * - MockMvc 로 실제 필터체인을 통과시킨다.
 * - bearer(...) 로 실제 JWT 를 만들어 Authorization 헤더로 인증한다.
 */
@IntegrationTest
public abstract class ApiSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtProvider jwtProvider;

    /** 주어진 권한을 담은 실제 access 토큰의 Authorization 헤더 값(Bearer ...)을 만든다. */
    protected String bearer(String username, String... roles) {
        return "Bearer " + jwtProvider.createAccessToken(username, List.of(roles));
    }
}
