package com.attendance.scheduler.infra.email;

import com.attendance.scheduler.teacher.dto.FindIdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * 실제 메일을 보내지 않고 로그만 남기는 구현체. test 프로파일에서 활성화되어 SMTP 의존 없이 동작한다.
 */
@Slf4j
@Service
@Profile({"test", "local"})
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendUserId(FindIdResponse findIdResponse) {
        // 로컬/테스트 편의를 위해 실제 값도 로그로 남긴다 (실제 메일 발송 없음)
        log.info("[console] sendUserId to {} → username={}", findIdResponse.email(), findIdResponse.username());
    }

    @Override
    public void sendAuthCode(String email, String authCode) {
        log.info("[console] sendAuthCode to {} → code={}", email, authCode);
    }
}
