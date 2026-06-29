package com.attendance.scheduler.comment.repository;

import com.attendance.scheduler.comment.dto.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.attendance.scheduler.comment.domain.entity.QComment.comment1;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    public final JPAQueryFactory queryFactory;

    public List<CommentResponse> getCommentList(Long id) {
        return queryFactory
                .select(Projections.constructor(CommentResponse.class,
                        comment1.id,
                        comment1.commentAuthor,
                        comment1.comment,
                        comment1.createdDate))
                .from(comment1)
                .where(comment1.board.id.eq(id))
                .fetch();
    }
}
