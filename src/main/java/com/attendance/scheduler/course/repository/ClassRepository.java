package com.attendance.scheduler.course.repository;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.attendance.scheduler.course.domain.QCourse.course;
import static com.attendance.scheduler.student.domain.QStudent.student;
import static com.attendance.scheduler.teacher.domain.QTeacher.teacher;

@Repository
@RequiredArgsConstructor
public class ClassRepository {

    public final JPAQueryFactory queryFactory;

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public List<ClassDTO> getStudentClassList(){
        return queryFactory
                .select(Projections.fields(ClassDTO.class,
                        student.studentName,
                        course.monday,
                        course.tuesday,
                        course.wednesday,
                        course.thursday,
                        course.friday,
                        teacher.teacherName,
                        course.updateTimeStamp))
                .from(course)
                .join(teacher)
                .on(course.teacherEntity.eq(teacher))
                .join(student)
                .on(course.studentEntity.eq(student))
                .fetch();
    }

    public StudentClassDTO getStudentClassByStudentName(String studentName){
        return queryFactory
                .select(Projections.fields(StudentClassDTO.class,
                        student.studentName,
                        course.monday,
                        course.tuesday,
                        course.wednesday,
                        course.thursday,
                        course.friday))
                .from(course)
                .where(student.studentName.eq(studentName))
                .fetchOne();
    }

    public List<StudentClassDTO> getStudentClassByTeacherEntity(Teacher teacher){
        return queryFactory
                .select(Projections.fields(StudentClassDTO.class,
                        course.monday,
                        course.tuesday,
                        course.wednesday,
                        course.thursday,
                        course.friday,
                        course.teacherEntity))
                .from(course)
                .where(course.teacherEntity.eq(teacher))
                .fetch();
    }

    public Optional<Course> getStudentClassEntityByStudentName(String studentName) {
        return Optional.ofNullable(queryFactory
                .selectFrom(course)
                .join(student)
                .on(course.studentEntity.studentName.eq(studentName))
                .fetchOne());
    }
}
