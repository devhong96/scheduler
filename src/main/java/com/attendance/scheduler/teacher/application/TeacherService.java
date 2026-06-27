package com.attendance.scheduler.teacher.application;

import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.StudentInformationDTO;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.student.repository.StudentRepository;
import com.attendance.scheduler.teacher.dto.JoinTeacherDTO;
import com.attendance.scheduler.teacher.dto.RegisterStudentDTO;
import com.attendance.scheduler.teacher.dto.StudentSearchCondition;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherJpaRepository teacherJpaRepository;
    private final StudentJpaRepository studentJpaRepository;
    private final ClassJpaRepository classJpaRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder teacherPasswordEncoder;

    @Transactional
    public void joinTeacher(JoinTeacherDTO joinTeacherDTO) {
        final String encode = teacherPasswordEncoder.encode(joinTeacherDTO.getPassword());
        joinTeacherDTO.setPassword(encode);
        teacherJpaRepository.save(joinTeacherDTO.toEntity());
    }

    public boolean findDuplicateTeacherID(JoinTeacherDTO joinTeacherDTO) {
        return teacherJpaRepository.existsByUsername(joinTeacherDTO.getUsername());
    }

    public boolean findDuplicateTeacherEmail(JoinTeacherDTO joinTeacherDTO) {
        return teacherJpaRepository.existsByEmail(joinTeacherDTO.getEmail());
    }

    @Transactional
    public void registerStudentInformation(RegisterStudentDTO registerStudentDTO) {
        Student studentEntity = registerStudentDTO.toEntity();
        studentEntity.setTeacherEntity(teacherJpaRepository.findByUsernameIs(registerStudentDTO.getTeacherUsername()));
        studentJpaRepository.save(studentEntity);
    }

    @Transactional
    public void deleteStudentInformation(StudentInformationDTO studentInformationDTO) {
        Optional<Student> studentEntityById = Optional.ofNullable(studentJpaRepository.findStudentEntityById(studentInformationDTO.getId()));
        //cascade
        studentEntityById.ifPresent(studentEntity -> classJpaRepository.deleteById(studentEntity.getId()));
        studentJpaRepository.deleteStudentEntityById(studentInformationDTO.getId());
    }

    @Transactional
    public Page<StudentInformationDTO> findStudentInformationList(StudentSearchCondition studentSearchCondition, Pageable pageable) {
        return studentRepository.studentInformationDTOList(studentSearchCondition, pageable);
    }
}
