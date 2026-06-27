package com.attendance.scheduler.board.domain;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.comment.domain.entity.Comment;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Board {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;
    private String content;

    @Column(columnDefinition = "integer default '0'")
    private Integer views;

    @CreationTimestamp
    private Timestamp creationTimestamp;

    @UpdateTimestamp
    private Timestamp modifiedDate;



    @ManyToOne(fetch = FetchType.LAZY)
    private Admin adminEntity;

    public void setAdminEntity(Admin adminEntity) {
        if (this.adminEntity != null) {
            this.adminEntity.getBoardEntityList().remove(this);
        }
        this.adminEntity = adminEntity;
        if(adminEntity != null){
            adminEntity.setBoardEntity(this);
        }
    }

    @OneToMany(mappedBy = "boardEntity")
    List<Comment> commentEntityList = new ArrayList<>();

    public void setCommentEntity(Comment commentEntity) {
        this.commentEntityList.add(commentEntity);
    }
    public void updateTitle(String title) {
        this.title = title;
    }
    public void updateContent(String content) {
        this.content = content;
    }



    @Builder
    public Board(String title, String content, Integer views, Timestamp creationTimestamp, Timestamp modifiedDate) {
        this.title = title;
        this.content = content;
        this.views = views;
        this.creationTimestamp = creationTimestamp;
        this.modifiedDate = modifiedDate;
    }
}
