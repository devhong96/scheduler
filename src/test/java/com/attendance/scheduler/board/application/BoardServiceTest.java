package com.attendance.scheduler.board.application;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.board.dto.BoardRequest;
import com.attendance.scheduler.board.repository.BoardJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired private BoardJpaRepository boardJpaRepository;
    @Autowired private AdminJpaRepository adminJpaRepository;

    @Test
    @Transactional
//    @Rollback(value = false)
    void pageNoticeList() {

        for (int i = 0; i < 150; i++) {
            BoardRequest boardRequest = new BoardRequest(0L, String.valueOf(i), "123", "관리자");
            Admin admin = adminJpaRepository.findByUsernameIs("admin");
            Board entity = boardRequest.toEntity();
            entity.setAdmin(admin);
            boardJpaRepository.save(entity);

        }
    }

    @Test
    void writeNotice() {

        //given //when
        for (int i = 0; i < 1; i++) {
            BoardRequest boardRequest = new BoardRequest(0L, String.valueOf(i), "123", "관리자");
            Admin admin = adminJpaRepository.findByUsernameIs("admin");
            Board entity = boardRequest.toEntity();
            entity.setAdmin(admin);
            boardJpaRepository.save(entity);
        }
        //then

        List<Board> all = boardJpaRepository.findAll();
        long count = all.size();
        Assertions.assertEquals(1, count);

    }
}
