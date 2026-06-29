package com.attendance.scheduler.comment.domain.entity;

import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.common.domain.BaseEntity;
import com.attendance.scheduler.student.domain.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String commentAuthor;

    @Column(nullable = false, length = 500)
    private String comment;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Board board;

    public void setBoard(Board boardEntity) {
        if (this.board != null) {
            this.board.getCommentEntityList().remove(this);
        }
        this.board = boardEntity;
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
    public Comment(String commentAuthor, String comment) {
        this.commentAuthor = commentAuthor;
        this.comment = comment;
    }
}
