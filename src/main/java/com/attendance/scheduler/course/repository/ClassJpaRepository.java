package com.attendance.scheduler.course.repository;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.teacher.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ClassJpaRepository extends JpaRepository<Course, Long> {

    /*
    * delete StudentName
    * */
    @Transactional
    void deleteByTeacherEntity(Teacher teacherEntity);

    void deleteClassEntityByStudentEntity(Student studentEntity);
}