package com.attendance.scheduler.comment.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String commentAuthor,
        String comment,
        LocalDateTime createdDate
) {
}
