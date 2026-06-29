package com.attendance.scheduler.infra.email;

public record EmailMessage(
        String from,
        String to,
        String subject,
        String message
) {
}
