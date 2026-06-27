package com.attendance.scheduler.board.application;

import com.attendance.scheduler.admin.domain.AdminEntity;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.board.domain.BoardEntity;
import com.attendance.scheduler.board.dto.BoardDTO;
import com.attendance.scheduler.board.repository.BoardJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired private BoardJpaRepository boardJpaRepository;
    @Autowired private AdminJpaRepository adminJpaRepository;

    @Test
    @Transactional
//    @Rollback(value = false)
    void pageNoticeList() {

        BoardDTO boardDTO = new BoardDTO();

        for (int i = 0; i < 150; i++) {
            boardDTO.setTitle(String.valueOf(i));
            boardDTO.setContent("123");
            boardDTO.setName("관리자");
            AdminEntity admin = adminJpaRepository.findByUsernameIs("admin");
            BoardEntity entity = boardDTO.toEntity();
            entity.setAdminEntity(admin);
            boardJpaRepository.save(entity);

        }
    }

    @Test
    void writeNotice() {

        //given //when
        BoardDTO boardDTO = new BoardDTO();

        for (int i = 0; i < 1; i++) {
            boardDTO.setTitle(String.valueOf(i));
            boardDTO.setContent("123");
            boardDTO.setName("관리자");
            AdminEntity admin = adminJpaRepository.findByUsernameIs("admin");
            BoardEntity entity = boardDTO.toEntity();
            entity.setAdminEntity(admin);
            boardJpaRepository.save(entity);
        }
        //then

        List<BoardEntity> all = boardJpaRepository.findAll();
        long count = all.size();
        Assertions.assertEquals(1, count);

    }
}