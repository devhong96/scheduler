package com.attendance.scheduler.comment.application;

import com.attendance.scheduler.board.domain.Board;
import com.attendance.scheduler.board.repository.BoardJpaRepository;
import com.attendance.scheduler.comment.domain.entity.Comment;
import com.attendance.scheduler.comment.dto.CommentRequest;
import com.attendance.scheduler.comment.dto.CommentResponse;
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

    public List<CommentResponse> getCommentList(Long id) {
        return commentRepository.getCommentList(id);
    }

    @Transactional
    public void saveComment(CommentRequest commentRequest) {
        Comment entity = commentRequest.toEntity();
        Board boardEntity = boardJpaRepository.findBoardEntityById(commentRequest.noticeId());
        Student studentEntity = studentJpaRepository.findStudentEntityByStudentName(commentRequest.commentAuthor());

        entity.setBoard(boardEntity);
        entity.setStudentEntity(studentEntity);

        commentJpaRepository.save(entity);
    }

    @Transactional
    public void deleteComment(CommentRequest commentRequest) {
        commentJpaRepository.deleteById(commentRequest.commentId());
    }
}
