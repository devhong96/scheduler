package com.attendance.scheduler.admin.dto;

public record EmailResponse(
        String username,
        String email
) {
    public EmailResponse withUsername(String username) {
        return new EmailResponse(username, this.email);
    }

    public EmailResponse withEmail(String email) {
        return new EmailResponse(this.username, email);
    }
}
