package com.attendance.scheduler.board.repository;

import com.attendance.scheduler.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardJpaRepository extends JpaRepository<Board, Long> {

    Board findBoardEntityById(Long id);
}
