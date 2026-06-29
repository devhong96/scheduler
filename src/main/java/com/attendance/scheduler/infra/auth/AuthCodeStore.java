package com.attendance.scheduler.infra.auth;

import java.util.Optional;

/**
 * 인증번호 저장소 추상화. 현재는 세션 기반 구현({@link SessionAuthCodeStore})만 있으며,
 * 멀티 인스턴스 확장 시 Redis 구현체를 추가하고 프로파일/프로퍼티로 교체하면 된다.
 */
public interface AuthCodeStore {

    void save(String username, AuthCode authCode);

    Optional<AuthCode> find(String username);

    void remove(String username);
}
