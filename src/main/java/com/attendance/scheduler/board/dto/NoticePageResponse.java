package com.attendance.scheduler.board.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/** 공지 목록(페이징) 응답. */
public record NoticePageResponse(
        List<BoardResponse> notices,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static NoticePageResponse of(Page<BoardResponse> page) {
        return new NoticePageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
