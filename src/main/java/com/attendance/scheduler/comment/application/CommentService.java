package com.attendance.scheduler.comment.application;

import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.board.repository.BoardJpaRepository;
import com.attendance.scheduler.comment.domain.entity.Comment;
import com.attendance.scheduler.comment.dto.CommentDTO;
import com.attendance.scheduler.comment.repository.CommentJpaRepository;
import com.attendance.scheduler.comment.repository.CommentRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    public final BoardJpaRepository boardJpaRepository;
    public final StudentJpaRepository studentJpaRepository;
    public final StudentRepository studentRepository;
    public final CommentRepository commentRepository;
    public final CommentJpaRepository commentJpaRepository;

    public List<CommentDTO> getCommentList(Long id) {
        return commentRepository.getCommentList(id);
    }

    @Transactional
    public void saveComment(CommentDTO commentDTO) {
        Comment entity = commentDTO.toEntity();
        Board boardEntity = boardJpaRepository.findBoardEntityById(commentDTO.getNoticeId());
        Student studentEntity = studentJpaRepository.findStudentEntityByStudentName(commentDTO.getCommentAuthor());

        entity.setBoardEntity(boardEntity);
        entity.setStudentEntity(studentEntity);

        commentJpaRepository.save(entity);
    }

    @Transactional
    public void deleteComment(CommentDTO commentDTO) {
        commentJpaRepository.deleteById(commentDTO.getCommentId());
    }
}
