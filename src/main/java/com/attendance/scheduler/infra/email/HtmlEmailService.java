package com.attendance.scheduler.infra.email;

import com.attendance.scheduler.teacher.dto.FindIdResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 실제 SMTP로 메일을 발송하는 구현체. test 프로파일 외(운영/로컬)에서 활성화된다.
 */
@Service
@Profile("!test & !local")
@Slf4j
public class HtmlEmailService implements EmailService {

    private final JavaMailSender javaMailSender;
    private final String from;

    public HtmlEmailService(JavaMailSender javaMailSender,
                            @Value("${spring.mail.username}") String from) {
        this.javaMailSender = javaMailSender;
        this.from = from;
    }

    @Override
    public void sendUserId(FindIdResponse findIdResponse) {
        sendEmail(new EmailMessage(
                from,
                findIdResponse.email(),
                "아이디 찾기",
                "가입하신 아이디는" +
                        System.lineSeparator() +
                        findIdResponse.username() + "입니다"
        ));
    }

    @Override
    public void sendAuthCode(String email, String authCode) {
        sendEmail(new EmailMessage(
                from,
                email,
                "비밀번호 찾기",
                "인증번호는 " + authCode + "입니다"
        ));
    }

    private void sendEmail(EmailMessage emailMessage) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper =
                    new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setFrom(emailMessage.from());
            mimeMessageHelper.setTo(emailMessage.to());
            mimeMessageHelper.setSubject(emailMessage.subject());
            mimeMessageHelper.setText(emailMessage.message(), true);
            javaMailSender.send(mimeMessage);
            log.info("sent email to {}", emailMessage.to());
        } catch (MessagingException e) {
            log.error("failed to send email to {}", emailMessage.to(), e);
            throw new RuntimeException(e);
        }
    }
}