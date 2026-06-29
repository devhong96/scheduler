package com.attendance.scheduler.board.domain;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.comment.domain.entity.Comment;
import com.attendance.scheduler.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(columnDefinition = "integer default '0'")
    private Integer views;

    @ManyToOne(fetch = FetchType.LAZY)
    private Admin admin;

    public void setAdmin(Admin admin) {
        if (this.admin != null) {
            this.admin.getBoardList().remove(this);
        }
        this.admin = admin;
        if(admin != null){
            admin.setBoard(this);
        }
    }

    @OneToMany(mappedBy = "board")
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
    public Board(String title, String content, Integer views) {
        this.title = title;
        this.content = content;
        this.views = views;
    }
}
