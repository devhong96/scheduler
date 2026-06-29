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
@Profile("test")
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendUserId(FindIdResponse findIdResponse) {
        log.info("[console] sendUserId to {}", findIdResponse.email());
    }

    @Override
    public void sendAuthCode(String email, String authCode) {
        log.info("[console] sendAuthCode to {}", email);
    }
}
