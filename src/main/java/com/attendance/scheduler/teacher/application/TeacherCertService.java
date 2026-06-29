package com.attendance.scheduler.teacher.application;

import com.attendance.scheduler.infra.email.FindPasswordRequest;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.EditEmailRequest;
import com.attendance.scheduler.teacher.dto.FindIdRequest;
import com.attendance.scheduler.teacher.dto.FindIdResponse;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TeacherCertService {

    private final TeacherJpaRepository teacherJpaRepository;
    private final PasswordEncoder teacherPasswordEncoder;

    public boolean idConfirmation(FindPasswordRequest findPasswordDTO) {
        return teacherJpaRepository
                .existsByUsername(findPasswordDTO.username());
    }

    public boolean emailConfirmation(FindPasswordRequest findPasswordDTO) {
        return teacherJpaRepository
                .existsByEmail(findPasswordDTO.email());
    }

    public Optional<FindIdResponse> findIdByEmail(FindIdRequest findIdRequest) {
        Optional<Teacher> optionalTeacherEntity
                = Optional.ofNullable(teacherJpaRepository.findByEmailIs(findIdRequest.email()));

        return optionalTeacherEntity.map(teacherEntity ->
                new FindIdResponse(teacherEntity.getEmail(), teacherEntity.getUsername())
        );
    }

    @Transactional
    public void initializePassword(PwdEditRequest pwdEditDTO) {
        final String encodePassword = teacherPasswordEncoder.encode(pwdEditDTO.password());
        pwdEditDTO = pwdEditDTO.withPassword(encodePassword);

        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(pwdEditDTO.username());
        teacherEntity.updatePassword(pwdEditDTO);
        teacherJpaRepository.save(teacherEntity);
    }

    @Transactional
    public void updateEmail(EditEmailRequest editEmailDTO) {
        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(editEmailDTO.username());
        teacherEntity.updateEmail(editEmailDTO);
        teacherJpaRepository.save(teacherEntity);
    }
}
