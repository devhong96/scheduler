package com.attendance.scheduler.admin.dto;

public record AdminResponse(
        String username,
        String password,
        String name,
        String email
) {
}
