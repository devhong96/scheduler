package com.attendance.scheduler.student.repository;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.student.dto.StudentInformationResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.attendance.scheduler.student.domain.QStudent.student;
import static com.attendance.scheduler.teacher.domain.QTeacher.teacher;

@ActiveProfiles("test")
@SpringBootTest
class StudentRepositoryTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Test
    void studentInformationDTOList() {

        List<StudentInformationResponse> studentInformationList = queryFactory
                .select(Projections.constructor(StudentInformationResponse.class,
                        student.id,
                        student.studentName,
                        student.studentPhoneNumber,
                        student.studentAddress,
                        student.studentDetailedAddress,
                        student.studentParentPhoneNumber,
                        teacher.teacherName,
                        student.createdDate))
                .from(student)
                .join(teacher)
                .on(student.teacherEntity.eq(teacher))
                .fetch();

        System.out.println("studentInformationList = " + studentInformationList);
    }

    @Test
    @Transactional
    void existStudentEntityByStudentNameAndStudentParentPhoneNumber() {
        Integer fetchOne = queryFactory.selectOne()
                .from(student)
                .where(student.studentName.eq("김샘플0"),
                        student.studentParentPhoneNumber.eq("010-1234-1234"))
                .fetchOne();

    }
}