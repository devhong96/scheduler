package com.attendance.scheduler.infra.email;

import com.attendance.scheduler.teacher.dto.FindIdResponse;

public interface EmailService {

    void sendUserId(FindIdResponse findIdResponse);

    void sendAuthCode(String email, String authCode);

}