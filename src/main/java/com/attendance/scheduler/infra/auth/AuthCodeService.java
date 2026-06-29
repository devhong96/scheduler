package com.attendance.scheduler.infra.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 비밀번호 찾기 등에 쓰이는 1회용 인증번호의 발급/검증을 담당한다.
 * 저장 위치(세션/Redis 등)는 {@link AuthCodeStore}로 분리되어 있고,
 * 이메일 발송기는 이 클래스가 만든 코드를 전달받아 보내기만 한다.
 */
@Service
@RequiredArgsConstructor
public class AuthCodeService {

    private static final int CODE_LENGTH = 6;
    private static final Duration TTL = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final AuthCodeStore authCodeStore;

    /**
     * 인증번호를 발급해 저장소에 보관하고 코드 문자열을 반환한다.
     */
    public String issue(String username) {
        String code = generateCode();
        authCodeStore.save(username, new AuthCode(code, LocalDateTime.now().plus(TTL)));
        return code;
    }

    /**
     * 입력한 인증번호를 검증한다. 성공/만료 시 해당 코드를 저장소에서 제거해 재사용을 막는다.
     */
    public AuthVerification verify(String username, String inputCode) {
        Optional<AuthCode> stored = authCodeStore.find(username);

        if (stored.isEmpty()) {
            return AuthVerification.NOT_ISSUED;
        }
        AuthCode authCode = stored.get();
        if (authCode.isExpired(LocalDateTime.now())) {
            authCodeStore.remove(username);
            return AuthVerification.EXPIRED;
        }
        if (!authCode.code().equals(inputCode)) {
            return AuthVerification.MISMATCH;
        }
        authCodeStore.remove(username);
        return AuthVerification.SUCCESS;
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(RANDOM.nextInt(10));
        }
        return code.toString();
    }
}
