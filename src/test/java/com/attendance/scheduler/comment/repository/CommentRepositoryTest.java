package com.attendance.scheduler.comment.repository;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.comment.dto.CommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.attendance.scheduler.comment.domain.entity.QComment.comment1;

@ActiveProfiles("test")
@SpringBootTest
@RequiredArgsConstructor
class CommentRepositoryTest {

    @Autowired
    public JPAQueryFactory queryFactory;

    @Test
    void getCommentList() {
        List<CommentResponse> fetch = queryFactory
                .select(Projections.constructor(CommentResponse.class,
                        comment1.id,
                        comment1.commentAuthor,
                        comment1.comment,
                        comment1.createdDate))
                .from(comment1)
                .where(comment1.board.id.eq(1L))
                .fetch();
    }
}