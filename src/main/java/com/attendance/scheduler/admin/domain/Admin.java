package com.attendance.scheduler.admin.domain;

import com.attendance.scheduler.admin.dto.EditEmailDTO;
import com.attendance.scheduler.board.domain.Board;
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
public class Admin {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "admin_id")
    private Long id;
    private String name;
    private String username;
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

    @OneToMany(mappedBy = "adminEntity")
    List<Board> boardEntityList = new ArrayList<>();

    public void setBoardEntity(Board boardEntity) {
        this.boardEntityList.add(boardEntity);
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateEmail(EditEmailDTO editEmailDTO) {
        this.email = editEmailDTO.getEmail();
    }
}
