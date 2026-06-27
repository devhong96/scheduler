package com.attendance.scheduler.comment.dto;

import com.attendance.scheduler.comment.domain.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter @Setter
@ToString
@NoArgsConstructor
public class CommentDTO {

    private Long id;
    private Long noticeId;
    private Long commentId;
    private String commentAuthor;
    private String password;
    private String comment;
    private Timestamp creationTimeStamp;

    public Comment toEntity() {
        return Comment.builder()
                .commentAuthor(commentAuthor)
                .comment(comment)
                .creationTimeStamp(creationTimeStamp)
                .build();
    }
}
