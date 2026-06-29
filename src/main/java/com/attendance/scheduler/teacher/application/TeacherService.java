package com.attendance.scheduler.teacher.application;

import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.StudentInformationRequest;
import com.attendance.scheduler.student.dto.StudentInformationResponse;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.student.repository.StudentRepository;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import com.attendance.scheduler.teacher.dto.RegisterStudentRequest;
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
    public void joinTeacher(JoinTeacherRequest joinTeacherDTO) {
        final String encode = teacherPasswordEncoder.encode(joinTeacherDTO.password());
        joinTeacherDTO = joinTeacherDTO.withPassword(encode);
        teacherJpaRepository.save(joinTeacherDTO.toEntity());
    }

    public boolean findDuplicateTeacherID(JoinTeacherRequest joinTeacherDTO) {
        return teacherJpaRepository.existsByUsername(joinTeacherDTO.username());
    }

    public boolean findDuplicateTeacherEmail(JoinTeacherRequest joinTeacherDTO) {
        return teacherJpaRepository.existsByEmail(joinTeacherDTO.email());
    }

    @Transactional
    public void registerStudentInformation(RegisterStudentRequest registerStudentDTO) {
        Student studentEntity = registerStudentDTO.toEntity();
        studentEntity.setTeacherEntity(teacherJpaRepository.findByUsernameIs(registerStudentDTO.teacherUsername()));
        studentJpaRepository.save(studentEntity);
    }

    @Transactional
    public void deleteStudentInformation(StudentInformationRequest studentInformationRequest) {
        Optional<Student> studentEntityById = Optional.ofNullable(
                studentJpaRepository.findStudentEntityById(studentInformationRequest.id()));
        studentEntityById.ifPresent(studentEntity -> classJpaRepository.deleteById(studentEntity.getId()));
        studentJpaRepository.deleteStudentEntityById(studentInformationRequest.id());
    }

    @Transactional
    public Page<StudentInformationResponse> findStudentInformationList(StudentSearchCondition studentSearchCondition, Pageable pageable) {
        return studentRepository.studentInformationDTOList(studentSearchCondition, pageable);
    }
}
