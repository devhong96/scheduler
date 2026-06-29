package com.attendance.scheduler.teacher.dto;

public record FindIdResponse(
        String email,
        String username
) {
    public FindIdResponse withEmail(String email) {
        return new FindIdResponse(email, username);
    }

    public FindIdResponse withUsername(String username) {
        return new FindIdResponse(email, username);
    }
}
