package com.attendance.scheduler.admin.domain;

import com.attendance.scheduler.admin.dto.EditEmailRequest;
import com.attendance.scheduler.board.domain.Board;
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
@Table(name = "admin")
@NoArgsConstructor(access = PROTECTED)
public class Admin extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(columnDefinition = "varchar(255) default '이메일을 입력해 주세요'")
    private String email;

    @Builder
    public Admin(Long id, String name, String username, String password, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @OneToMany(mappedBy = "admin")
    List<Board> boardList = new ArrayList<>();

    public void setBoard(Board boardEntity) {
        this.boardList.add(boardEntity);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(EditEmailRequest editEmailDTO) {
        this.email = editEmailDTO.email();
    }
}
