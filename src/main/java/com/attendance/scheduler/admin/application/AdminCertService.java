package com.attendance.scheduler.admin.application;

import com.attendance.scheduler.admin.domain.AdminEntity;
import com.attendance.scheduler.admin.dto.EditEmailDTO;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.infra.email.FindPasswordDTO;
import com.attendance.scheduler.teacher.dto.PwdEditDTO;
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

    public boolean emailConfirmation(FindPasswordDTO findPasswordDTO) {
        return adminJpaRepository.existsByEmail(findPasswordDTO.getEmail());
    }

    @Transactional
    public void initializePassword(PwdEditDTO pwdEditDTO) {
        final String encodePassword = passwordEncoder.encode(pwdEditDTO.getPassword());
        AdminEntity adminEntity = adminJpaRepository
                .findByUsernameIs(pwdEditDTO.getUsername());
        adminEntity.updatePassword(encodePassword);
        adminJpaRepository.save(adminEntity);
    }

    @Transactional
    public void updateEmail(EditEmailDTO editEmailDTO) {
        AdminEntity adminEntity = adminJpaRepository
                .findByUsernameIs(editEmailDTO.getUsername());
        adminEntity.updateEmail(editEmailDTO);
        adminJpaRepository.save(adminEntity);
    }
}
