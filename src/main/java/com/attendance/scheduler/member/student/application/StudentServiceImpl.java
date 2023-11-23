package com.attendance.scheduler.member.student.application;

import com.attendance.scheduler.member.student.repository.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentJpaRepository studentJpaRepository;

    @Override
    public boolean existStudentEntityByStudentName(String studentName) {
        return studentJpaRepository.existsByStudentNameIs(studentName);
    }
}
