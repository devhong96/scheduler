package com.attendance.scheduler.admin.application;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.dto.EditEmailRequest;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.infra.email.FindPasswordRequest;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminCertService{

    private final PasswordEncoder passwordEncoder;
    private final AdminJpaRepository adminJpaRepository;

    public boolean emailConfirmation(FindPasswordRequest findPasswordDTO) {
        return adminJpaRepository.existsByEmail(findPasswordDTO.email());
    }

    @Transactional
    public void initializePassword(PwdEditRequest pwdEditDTO) {
        final String encodePassword = passwordEncoder.encode(pwdEditDTO.password());
        Admin adminEntity = adminJpaRepository
                .findByUsernameIs(pwdEditDTO.username());
        adminEntity.updatePassword(encodePassword);
        adminJpaRepository.save(adminEntity);
    }

    @Transactional
    public void updateEmail(EditEmailRequest editEmailDTO) {
        Admin adminEntity = adminJpaRepository
                .findByUsernameIs(editEmailDTO.username());
        adminEntity.updateEmail(editEmailDTO);
        adminJpaRepository.save(adminEntity);
    }
}
