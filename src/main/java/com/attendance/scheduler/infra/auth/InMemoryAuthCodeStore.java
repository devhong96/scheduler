package com.attendance.scheduler.infra.auth;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 인증번호 저장소. stateless(JWT) 환경에서 세션 없이 두 요청 간 코드를 유지한다.
 * 단일 인스턴스 배포에 적합하며, 멀티 인스턴스 확장 시 Redis 구현체로 교체하면 된다.
 * 만료된 코드는 조회 시 정리한다.
 */
@Component
public class InMemoryAuthCodeStore implements AuthCodeStore {

    private final Map<String, AuthCode> store = new ConcurrentHashMap<>();

    @Override
    public void save(String username, AuthCode authCode) {
        store.put(username, authCode);
    }

    @Override
    public Optional<AuthCode> find(String username) {
        AuthCode authCode = store.get(username);
        if (authCode == null) {
            return Optional.empty();
        }
        if (authCode.isExpired(LocalDateTime.now())) {
            store.remove(username);
            return Optional.empty();
        }
        return Optional.of(authCode);
    }

    @Override
    public void remove(String username) {
        store.remove(username);
    }
}
