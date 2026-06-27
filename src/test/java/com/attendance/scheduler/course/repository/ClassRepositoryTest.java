package com.attendance.scheduler.course.repository;

import com.attendance.scheduler.course.dto.ClassDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.attendance.scheduler.course.domain.QCourse.course;
import static com.attendance.scheduler.student.domain.QStudent.student;
import static com.attendance.scheduler.teacher.domain.QTeacher.teacher;

@SpringBootTest
class ClassRepositoryTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    public void getStudentClassList(){
        List<ClassDTO> fetch = queryFactory
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
                .join(course)
                .on(course.studentEntity.eq(student))
                .fetch();
        System.out.println("fetch = " + fetch);
    }

    @Test
    public void getStudentClassByTeacherEntity(){

    }
}