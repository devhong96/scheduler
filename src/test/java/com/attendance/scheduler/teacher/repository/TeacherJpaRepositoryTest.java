package com.attendance.scheduler.teacher.repository;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.infra.config.JpaAuditingConfig;
import com.attendance.scheduler.teacher.domain.Teacher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.testTeacherDataSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

@ActiveProfiles("test")
@DataJpaTest
@Import(JpaAuditingConfig.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TeacherJpaRepositoryTest {

    @Autowired
    private TeacherJpaRepository teacherJpaRepository;

    @BeforeEach
    void beforeEachTest(){
        boolean b = teacherJpaRepository.existsByUsername(testTeacherDataSet().username());
        if(!b) {
            teacherJpaRepository.save(testTeacherDataSet().toEntity());
        }
    }

    @Test
    void existsByUsername() {

        //When
        boolean existsByUsername = teacherJpaRepository
                .existsByUsername(testTeacherDataSet().username());

        //Then
        assertTrue(existsByUsername);
    }

    @Test
    void existsByEmail() {

        //When
        boolean existsByEmail = teacherJpaRepository
                .existsByEmail(testTeacherDataSet().email());

        //Then
        assertTrue(existsByEmail);
    }

    @Test
    void findByUsernameIs() {
        //When
        Teacher byUsernameIs = teacherJpaRepository
                .findByUsernameIs(testTeacherDataSet().username());
        //Then
        assertEquals(testTeacherDataSet().username(), byUsernameIs.getUsername());
        assertEquals(testTeacherDataSet().email(), byUsernameIs.getEmail());
    }

    @Test
    void findByEmailIs() {

        //When
        Optional<Teacher> byEmailIs = Optional.ofNullable(teacherJpaRepository
                .findByEmailIs(testTeacherDataSet().email()));
        //Then
        if(byEmailIs.isPresent()) {
            assertEquals(testTeacherDataSet().username(), byEmailIs.get().getUsername());
            assertEquals(testTeacherDataSet().email(), byEmailIs.get().getEmail());
        }
    }

    @Test
    void deleteByUsernameIs() {
    }
}
