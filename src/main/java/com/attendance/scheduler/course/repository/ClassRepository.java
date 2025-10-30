package com.attendance.scheduler.course.repository;

import com.attendance.scheduler.course.domain.ClassEntity;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.teacher.domain.TeacherEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.attendance.scheduler.course.domain.QClassEntity.classEntity;
import static com.attendance.scheduler.student.domain.QStudentEntity.studentEntity;
import static com.attendance.scheduler.teacher.domain.QTeacherEntity.teacherEntity;

@Repository
@RequiredArgsConstructor
public class ClassRepository {

    public final JPAQueryFactory queryFactory;

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<ClassDTO> getStudentClassList(){
        return queryFactory
                .select(Projections.fields(ClassDTO.class,
                        studentEntity.studentName,
                        classEntity.monday,
                        classEntity.tuesday,
                        classEntity.wednesday,
                        classEntity.thursday,
                        classEntity.friday,
                        teacherEntity.teacherName,
                        classEntity.updateTimeStamp))
                .from(classEntity)
                .join(teacherEntity)
                .on(classEntity.teacherEntity.eq(teacherEntity))
                .join(studentEntity)
                .on(classEntity.studentEntity.eq(studentEntity))
                .fetch();
    }

    public StudentClassDTO getStudentClassByStudentName(String studentName){
        return queryFactory
                .select(Projections.fields(StudentClassDTO.class,
                        studentEntity.studentName,
                        classEntity.monday,
                        classEntity.tuesday,
                        classEntity.wednesday,
                        classEntity.thursday,
                        classEntity.friday))
                .from(classEntity)
                .where(studentEntity.studentName.eq(studentName))
                .fetchOne();
    }

    public List<StudentClassDTO> getStudentClassByTeacherEntity(TeacherEntity teacherEntity){
        return queryFactory
                .select(Projections.fields(StudentClassDTO.class,
                        classEntity.monday,
                        classEntity.tuesday,
                        classEntity.wednesday,
                        classEntity.thursday,
                        classEntity.friday,
                        classEntity.teacherEntity))
                .from(classEntity)
                .where(classEntity.teacherEntity.eq(teacherEntity))
                .fetch();
    }

    public Optional<ClassEntity> getStudentClassEntityByStudentName(String studentName) {
        return Optional.ofNullable(queryFactory
                .selectFrom(classEntity)
                .join(studentEntity)
                .on(classEntity.studentEntity.studentName.eq(studentName))
                .fetchOne());
    }
}
