package com.attendance.scheduler.admin.repository;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.infra.config.JpaAuditingConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AdminJpaRepositoryTest {

    @Autowired
    private AdminJpaRepository adminJpaRepository;

    // 슬라이스 테스트에는 AdminAccountInitializer(ApplicationRunner)가 동작하지 않으므로 직접 시드한다.
    @BeforeEach
    void seedAdmin() {
        if (adminJpaRepository.findByUsernameIs("admin") == null) {
            adminJpaRepository.save(Admin.builder()
                    .username("admin")
                    .name("관리자")
                    .email("adminTest@gmail.com")
                    .password("encoded")
                    .build());
        }
    }

    @Test
    void findByUsernameIs() {

        //When
        Admin byUsernameIs = adminJpaRepository
                .findByUsernameIs("admin");

        //Then
        assertEquals("admin", byUsernameIs.getUsername());
    }
}
