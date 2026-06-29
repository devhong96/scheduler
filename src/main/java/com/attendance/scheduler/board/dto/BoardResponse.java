package com.attendance.scheduler.board.dto;

import java.time.LocalDateTime;

public record BoardResponse(
        Long id,
        String title,
        String content,
        String name,
        Integer views,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {
}
