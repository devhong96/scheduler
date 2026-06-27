package com.attendance.scheduler.admin.repository;

import com.attendance.scheduler.admin.domain.Admin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AdminJpaRepositoryTest {

    @Autowired
    private AdminJpaRepository adminJpaRepository;

    @Test
    void findByUsernameIs() {

        //When
        Admin byUsernameIs = adminJpaRepository
                .findByUsernameIs("admin");

        //Then
        assertEquals("admin", byUsernameIs.getUsername());
    }
}