package com.attendance.scheduler.student.repository;

import com.attendance.scheduler.comment.dto.CommentDTO;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.StudentInformationDTO;
import com.attendance.scheduler.teacher.dto.StudentSearchCondition;
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
import java.util.Optional;

import static com.attendance.scheduler.student.domain.QStudent.student;
import static com.attendance.scheduler.teacher.domain.QTeacher.teacher;
import static org.springframework.util.StringUtils.hasText;


@Repository
@RequiredArgsConstructor
public class StudentRepository {

    public final JPAQueryFactory queryFactory;

    public Page<StudentInformationDTO> studentInformationDTOList(StudentSearchCondition studentSearchCondition, Pageable pageable){
        List<StudentInformationDTO> studentInformationList = queryFactory
                .select(Projections.fields(StudentInformationDTO.class,
                        student.id,
                        student.studentName,
                        student.studentAddress,
                        student.studentDetailedAddress,
                        student.studentPhoneNumber,
                        student.studentParentPhoneNumber,
                        teacher.teacherName,
                        student.creationTimestamp))
                .from(student)
                .join(teacher)
                .on(student.teacherEntity.eq(teacher))
                .where(
                        studentNameEq(studentSearchCondition.getStudentName()),
                        teacherNameEq(studentSearchCondition.getTeacherName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        System.out.println("studentInformationList = " + studentInformationList);

        JPAQuery<Long> counts = queryFactory
                .select(student.count())
                .from(student)
                .where(
                        studentNameEq(studentSearchCondition.getStudentName()),
                        teacherNameEq(studentSearchCondition.getTeacherName())
                );

        return PageableExecutionUtils.getPage(studentInformationList, pageable, counts::fetchOne);
    }

    private BooleanExpression studentNameEq(String studentName) {
        return hasText(studentName) ? student.studentName.eq(studentName) : null;
    }

    private BooleanExpression teacherNameEq(String teacherName) {
        return hasText(teacherName) ? teacher.teacherName.eq(teacherName) : null;
    }

    public Optional<Student> getStudentEntity(Long studentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(student)
                .where(student.id.eq(studentId))
                .fetchOne());
    }




    public boolean existStudentEntityByStudentNameAndStudentParentPhoneNumber(CommentDTO commentDTO) {
        Integer fetchOne = queryFactory.selectOne()
                .from(student)
                .where(student.studentName.eq(commentDTO.getCommentAuthor()),
                        student.studentParentPhoneNumber.eq(commentDTO.getPassword()))
                .fetchOne();
        return fetchOne!=null;
    }
}
