package com.attendance.scheduler.student.application;

import com.attendance.scheduler.comment.dto.CommentDTO;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentJpaRepository studentJpaRepository;
    private final StudentRepository studentRepository;

    public boolean existStudentEntityByStudentName(String studentName) {
        return studentJpaRepository.existsByStudentNameIs(studentName);
    }

    public boolean existStudentEntityByStudentNameAndStudentParentPhoneNumber(CommentDTO commentDTO) {
        return studentRepository.existStudentEntityByStudentNameAndStudentParentPhoneNumber(commentDTO);
    }
}
