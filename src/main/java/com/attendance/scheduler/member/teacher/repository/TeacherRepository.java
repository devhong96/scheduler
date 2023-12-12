package com.attendance.scheduler.member.teacher.repository;

import com.attendance.scheduler.member.teacher.dto.TeacherDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.attendance.scheduler.member.teacher.domain.QTeacherEntity.teacherEntity;


@Repository
@RequiredArgsConstructor
public class TeacherRepository {
    public final JPAQueryFactory queryFactory;

    public List<TeacherDTO> getTeacherList(){
        return queryFactory
                .select(Projections.fields(TeacherDTO.class,
                        teacherEntity.id,
                        teacherEntity.username,
                        teacherEntity.teacherName,
                        teacherEntity.approved
                        ))
                .from(teacherEntity)
                .fetch();
    }

    public List<TeacherDTO> getTeacherInfoByUsername(String username){
        return queryFactory
                .select(Projections.fields(TeacherDTO.class,
                        teacherEntity.id,
                        teacherEntity.username,
                        teacherEntity.teacherName,
                        teacherEntity.approved
                ))
                .from(teacherEntity)
                .where(teacherEntity.username.eq(username))
                .fetch();
    }
}