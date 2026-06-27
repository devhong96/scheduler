package com.attendance.scheduler.teacher.repository;

import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherJpaRepository extends JpaRepository<Teacher, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Teacher findByUsernameIs(String username);

    Teacher findTeacherEntityById(Long id);

    Teacher findByEmailIs(String email);

    @Transactional
    void deleteByUsernameIs(String username);

}
