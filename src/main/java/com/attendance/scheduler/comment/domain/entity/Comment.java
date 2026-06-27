package com.attendance.scheduler.comment.domain.entity;

import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.student.domain.Student;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String commentAuthor;
    private String comment;

    @CreationTimestamp
    private Timestamp creationTimeStamp;

    @ManyToOne(fetch = LAZY)
    private Board boardEntity;

    public void setBoardEntity(Board boardEntity) {
        if (this.boardEntity != null) {
            this.boardEntity.getCommentEntityList().remove(this);
        }
        this.boardEntity = boardEntity;
        if (boardEntity != null) {
            boardEntity.setCommentEntity(this);
        }
    }

    @ManyToOne(fetch = LAZY)
    private Student studentEntity;

    public void setStudentEntity(Student studentEntity) {
        if (this.studentEntity != null) {
            this.studentEntity.getCommentEntityList().remove(this);
        }
        this.studentEntity = studentEntity;
        if (studentEntity != null) {
            studentEntity.addCommentEntity(this);
        }
    }

    @Builder
    public Comment(String commentAuthor, String comment, Timestamp creationTimeStamp) {
        this.commentAuthor = commentAuthor;
        this.comment = comment;
        this.creationTimeStamp = creationTimeStamp;
    }
}
