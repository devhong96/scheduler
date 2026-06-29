package com.attendance.scheduler.comment.dto;

import com.attendance.scheduler.comment.domain.entity.Comment;

public record CommentRequest(
        Long noticeId,
        Long commentId,
        String commentAuthor,
        String password,
        String comment
) {
    public Comment toEntity() {
        return Comment.builder()
                .commentAuthor(commentAuthor)
                .comment(comment)
                .build();
    }
}
