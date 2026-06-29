package com.attendance.scheduler.board.application;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.board.dto.BoardRequest;
import com.attendance.scheduler.board.dto.BoardResponse;
import com.attendance.scheduler.board.dto.Condition;
import com.attendance.scheduler.board.repository.BoardJpaRepository;
import com.attendance.scheduler.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardJpaRepository boardJpaRepository;
    private final BoardRepository boardRepository;
    private final AdminJpaRepository adminJpaRepository;

    public Page<BoardResponse> pageNoticeList(Condition condition, Pageable pageable) {
        return boardRepository.pageNoticeList(condition, pageable);
    }

    @Transactional
    public void writeNotice(BoardRequest boardRequest) {
        Admin admin = adminJpaRepository.findByUsernameIs(boardRequest.name());
        Board entity = boardRequest.toEntity();
        entity.setAdmin(admin);
        boardJpaRepository.save(entity);
    }

    @Transactional
    public BoardResponse findNoticeById(Long id) {
        return boardRepository.findNoticeById(id);
    }

    public BoardResponse editNoticeForm(Long id) {
        return boardRepository.editNoticeForm(id);
    }

    @Transactional
    public void editNotice(BoardRequest boardRequest) {
        Board boardEntityById = boardJpaRepository.findBoardEntityById(boardRequest.id());
        boardEntityById.updateTitle(boardRequest.title());
        boardEntityById.updateContent(boardRequest.content());
        boardJpaRepository.save(boardEntityById);
    }

    @Transactional
    public void deleteNotice(Long id) {
        boardJpaRepository.deleteById(id);
    }
}
