package com.attendance.scheduler.Repository.jpa;

import com.attendance.scheduler.Admin.AdminEntity;
import com.attendance.scheduler.Admin.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    void findByUsernameIs() {

        //When
        AdminEntity byUsernameIs = adminRepository
                .findByUsernameIs("admin");

        //Then
        assertEquals("admin", byUsernameIs.getUsername());
    }
}