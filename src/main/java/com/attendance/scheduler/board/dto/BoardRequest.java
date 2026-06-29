package com.attendance.scheduler.board.dto;

import com.attendance.scheduler.board.domain.Board;

public record BoardRequest(
        Long id,
        String title,
        String content,
        String name
) {
    public BoardRequest withId(Long id) {
        return new BoardRequest(id, title, content, name);
    }

    public BoardRequest withName(String name) {
        return new BoardRequest(id, title, content, name);
    }

    public BoardRequest withTitle(String title) {
        return new BoardRequest(id, title, content, name);
    }

    public BoardRequest withContent(String content) {
        return new BoardRequest(id, title, content, name);
    }

    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .views(0)
                .build();
    }
}
