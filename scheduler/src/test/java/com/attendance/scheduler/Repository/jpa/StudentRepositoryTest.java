package com.attendance.scheduler.Repository.jpa;

import com.attendance.scheduler.Student.StudentEntity;
import com.attendance.scheduler.Student.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.attendance.scheduler.Config.TestDataSet.testStudentInformationDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void beforeEach(){
        studentRepository.save(testStudentInformationDTO().toEntity());
    }

    @Test
    @DisplayName("학생 인적 사항 정보 저장")
    void findStudentEntityByStudentName() {
        Optional<StudentEntity> studentEntityByStudentName = studentRepository
                .findStudentEntityByStudentNameIs(testStudentInformationDTO().getStudentName());

        assertEquals(testStudentInformationDTO().getStudentName(),
                studentEntityByStudentName.get().getStudentName());
    }

    @Test //TODO
    @DisplayName("학생 정보 찾기(고유번호로)")
    void findStudentEntityById(){

    }

    @Test
    @DisplayName("학생 정보 삭제(이름이 아닌 고유번호로)")
    void deleteStudentEntityById() {
        Optional<StudentEntity> studentEntityByStudentName = studentRepository
                .findStudentEntityByStudentNameIs(testStudentInformationDTO().getStudentName());

        studentRepository.deleteStudentEntityById(studentEntityByStudentName.get().getId());

        assertNull(studentRepository.findStudentEntityById(studentEntityByStudentName.get().getId()));
    }
}