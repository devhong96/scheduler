package com.attendance.scheduler.board.repository;

import com.attendance.scheduler.board.dto.BoardDTO;
import com.attendance.scheduler.board.dto.Condition;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.attendance.scheduler.admin.domain.QAdmin.admin;
import static com.attendance.scheduler.board.domain.QBoard.board;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    public final JPAQueryFactory queryFactory;


    public Page<BoardDTO> pageNoticeList(Condition condition, Pageable pageable){
        List<BoardDTO> content = queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.id,
                        board.title,
                        board.content,
                        admin.name,
                        board.views,
                        board.creationTimestamp,
                        board.modifiedDate))
                .from(board)
                .join(admin)
                .on(board.adminEntity.id.eq(admin.id))
                .where(
                        titleEq(condition.getTitleContent()),
                        contentEq(condition.getTitleContent())
                )
                .orderBy(board.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> counts = queryFactory
                .select(board.count())
                .from(board)
                .where(
                        titleEq(condition.getTitleContent()),
                        contentEq(condition.getTitleContent())
                );

        return PageableExecutionUtils.getPage(content, pageable, counts::fetchOne);
    }

    private BooleanExpression titleEq(String title){
        return hasText(title) ? board.title.eq(title) : null;
    }

    private BooleanExpression contentEq(String content){
        return hasText(content) ? board.content.eq(content) : null;
    }





    public BoardDTO findNoticeById(Long id) {

        queryFactory
                .update(board)
                .set(board.views, board.views.add(1))
                .where(board.id.eq(id))
                .execute();

        return queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.id,
                        board.title,
                        board.content,
                        admin.name,
                        board.views,
                        board.creationTimestamp))
                .from(board)
                .join(admin)
                .on(board.adminEntity.id.eq(admin.id))
                .where(board.id.eq(id))
                .fetchOne();
    }

    public BoardDTO editNoticeForm(Long id){
        return queryFactory
                .select(Projections.fields(BoardDTO.class,
                        board.id,
                        board.title,
                        board.content,
                        admin.name,
                        board.views,
                        board.creationTimestamp,
                        board.modifiedDate))
                .from(board)
                .join(admin)
                .on(board.adminEntity.id.eq(admin.id))
                .where(board.id.eq(id))
                .fetchOne();

    }
}
