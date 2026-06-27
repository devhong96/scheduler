package com.attendance.scheduler.infra.config.security.Admin;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner{

    private final AdminJpaRepository adminJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Optional<Admin> optionalAdminEntity = Optional
                .ofNullable(adminJpaRepository.findByUsernameIs("admin"));
        optionalAdminEntity.orElseGet(() -> adminJpaRepository.save(
                Admin.builder()
                        .username("admin")
                        .email("adminTest@gmail.com")
                        .name("관리자")
                        .password(passwordEncoder.encode("root123!@#"))
                        .build()));
    }

    @Scheduled(fixedRate = 3600000)
    public void updatePassword(){
        Optional<Admin> optionalAdminEntity = Optional
                .ofNullable(adminJpaRepository
                        .findByUsernameIs("admin"));
        optionalAdminEntity.ifPresent(
                adminEntity -> {
                    adminEntity.updatePassword(passwordEncoder.encode("root123!@#"));
                    adminJpaRepository.save(adminEntity);
        });
    }
}