package com.attendance.scheduler.infra.auth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * HttpSession 기반 인증번호 저장소. 단일 인스턴스 배포에 적합하다.
 * 주입되는 HttpSession은 Spring이 제공하는 요청 스코프 프록시로, 현재 요청의 세션으로 위임된다.
 */
@Component
@RequiredArgsConstructor
public class SessionAuthCodeStore implements AuthCodeStore {

    private static final String SESSION_KEY_PREFIX = "authCode:";

    private final HttpSession session;

    @Override
    public void save(String username, AuthCode authCode) {
        session.setAttribute(sessionKey(username), authCode);
    }

    @Override
    public Optional<AuthCode> find(String username) {
        return Optional.ofNullable((AuthCode) session.getAttribute(sessionKey(username)));
    }

    @Override
    public void remove(String username) {
        session.removeAttribute(sessionKey(username));
    }

    private String sessionKey(String username) {
        return SESSION_KEY_PREFIX + username;
    }
}
