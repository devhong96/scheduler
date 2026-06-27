package com.attendance.scheduler.student.repository;

import com.attendance.scheduler.student.domain.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentJpaRepository extends JpaRepository<Student, Long> {

    boolean existsByStudentNameIs(String StudentName);

    Student findStudentEntityById(Long id);


    Student findStudentEntityByStudentName(String studentName);

    @Transactional
    void deleteStudentEntityById(long id);
}