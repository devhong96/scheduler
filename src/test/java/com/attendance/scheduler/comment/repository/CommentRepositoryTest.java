package com.attendance.scheduler.comment.repository;

import com.attendance.scheduler.comment.dto.CommentDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.attendance.scheduler.comment.domain.entity.QComment.comment1;

@SpringBootTest
@RequiredArgsConstructor
class CommentRepositoryTest {

    @Autowired
    public JPAQueryFactory queryFactory;

    @Test
    void getCommentList() {
        List<CommentDTO> fetch = queryFactory
                .select(Projections.fields(CommentDTO.class,
                        comment1.id,
                        comment1.commentAuthor,
                        comment1.comment,
                        comment1.creationTimeStamp))
                .from(comment1)
                .where(comment1.boardEntity.id.eq(1L))
                .fetch();
    }
}