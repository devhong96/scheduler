package com.attendance.scheduler.teacher.application;

import com.attendance.scheduler.infra.email.FindPasswordDTO;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.EditEmailDTO;
import com.attendance.scheduler.teacher.dto.FindIdDTO;
import com.attendance.scheduler.teacher.dto.PwdEditDTO;
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

    public boolean idConfirmation(FindPasswordDTO findPasswordDTO) {
        return teacherJpaRepository
                .existsByUsername(findPasswordDTO.getUsername());
    }

    public boolean emailConfirmation(FindPasswordDTO findPasswordDTO) {
        return teacherJpaRepository
                .existsByEmail(findPasswordDTO.getEmail());
    }

    public Optional<FindIdDTO> findIdByEmail(FindIdDTO findIdDTO) {
        Optional<Teacher> optionalTeacherEntity
                = Optional.ofNullable(teacherJpaRepository.findByEmailIs(findIdDTO.getEmail()));

        return optionalTeacherEntity.map(teacherEntity -> {
            FindIdDTO resultDTO = new FindIdDTO();
            resultDTO.setUsername(teacherEntity.getUsername());
            resultDTO.setEmail(teacherEntity.getEmail());
            return resultDTO;
        });
    }

    @Transactional
    public void initializePassword(PwdEditDTO pwdEditDTO) {
        final String encodePassword = teacherPasswordEncoder.encode(pwdEditDTO.getPassword());
        pwdEditDTO.setPassword(encodePassword);

        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(pwdEditDTO.getUsername());
        teacherEntity.updatePassword(pwdEditDTO);
        teacherJpaRepository.save(teacherEntity);
    }

    @Transactional
    public void updateEmail(EditEmailDTO editEmailDTO) {
        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(editEmailDTO.getUsername());
        teacherEntity.updateEmail(editEmailDTO);
        teacherJpaRepository.save(teacherEntity);
    }
}
