package com.attendance.scheduler.course.repository;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.course.dto.ClassResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.attendance.scheduler.course.domain.QCourse.course;
import static com.attendance.scheduler.student.domain.QStudent.student;
import static com.attendance.scheduler.teacher.domain.QTeacher.teacher;

@ActiveProfiles("test")
@SpringBootTest
class ClassRepositoryTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    public void getStudentClassList(){
        List<ClassResponse> fetch = queryFactory
                .select(Projections.constructor(ClassResponse.class,
                        student.studentName,
                        course.monday,
                        course.tuesday,
                        course.wednesday,
                        course.thursday,
                        course.friday,
                        teacher.teacherName,
                        course.lastModifiedDate))
                .from(course)
                .join(teacher)
                .on(course.teacherEntity.eq(teacher))
                .join(student)
                .on(course.studentEntity.eq(student))
                .fetch();
        System.out.println("fetch = " + fetch);
    }

    @Test
    public void getStudentClassByTeacherEntity(){

    }
}