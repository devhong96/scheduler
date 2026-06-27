package com.attendance.scheduler.board.dto;

import com.attendance.scheduler.board.domain.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter @Setter
@ToString
@NoArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String content;
    private String name;
    private Integer views;
    private Timestamp creationTimestamp;
    private Timestamp modifiedDate;

    public Board toEntity(){
        return Board.builder()
                .title(title)
                .content(content)
                .views(views)
                .creationTimestamp(creationTimestamp)
                .modifiedDate(modifiedDate)
                .build();
    }
}
