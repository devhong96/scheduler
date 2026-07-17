package com.attendance.scheduler.board.dto;

import com.attendance.scheduler.comment.dto.CommentResponse;

import java.util.List;

/** 공지 상세 + 댓글 목록 응답. */
public record NoticeDetailResponse(
        BoardResponse notice,
        List<CommentResponse> comments
) {
}
