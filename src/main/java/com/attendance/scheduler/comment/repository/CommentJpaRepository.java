package com.attendance.scheduler.comment.repository;

import com.attendance.scheduler.comment.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
}
