package com.attendance.scheduler.testDataSet;

import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.StudentInformationDTO;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.JoinTeacherDTO;
import com.attendance.scheduler.teacher.dto.RegisterStudentDTO;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SampleDataTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private ClassService classService;

    @Autowired
    private StudentJpaRepository studentJpaRepository;

    @Autowired
    private TeacherJpaRepository teacherJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JoinTeacherDTO sampleTeacherDataSet(){
        JoinTeacherDTO joinTeacherDTO = new JoinTeacherDTO();
        joinTeacherDTO.setUsername("sampleTeacher");
        joinTeacherDTO.setPassword("123");
        joinTeacherDTO.setEmail("sampleTeacherDataSet@gmail.com");
        joinTeacherDTO.setTeacherName("김교사");
        joinTeacherDTO.setApproved(true);
        return joinTeacherDTO;
    }

    public JoinTeacherDTO sample2TeacherDataSet(){
        JoinTeacherDTO joinTeacherDTO = new JoinTeacherDTO();
        joinTeacherDTO.setUsername("sample2Teacher");
        joinTeacherDTO.setPassword("123");
        joinTeacherDTO.setEmail("sample2TeacherDataSet@gmail.com");
        joinTeacherDTO.setTeacherName("박교사");
        joinTeacherDTO.setApproved(true);
        return joinTeacherDTO;
    }

    public static StudentInformationDTO sampleStudentInformationDTO() {
        StudentInformationDTO studentInformationDTO = new StudentInformationDTO();
        studentInformationDTO.setStudentName("김샘플");
        studentInformationDTO.setStudentPhoneNumber("010-1234-1234");
        studentInformationDTO.setStudentParentPhoneNumber("010-1234-1233");
        studentInformationDTO.setStudentAddress("대한민국 저기 어디");
        studentInformationDTO.setStudentDetailedAddress("어디");
        return studentInformationDTO;
    }

    public ClassDTO sampleClass(){
        ClassDTO classDTO = new ClassDTO();
        classDTO.setStudentName("sampleStudent");
        classDTO.setMonday(1);
        classDTO.setTuesday(2);
        classDTO.setWednesday(3);
        classDTO.setThursday(4);
        classDTO.setFriday(5);
        return classDTO;
    }
        @Test
        @DisplayName("샘플 교사 정보")
        void saveSampleTeacherDataSet(){
            String encode = passwordEncoder.encode(sampleTeacherDataSet().getPassword());
            sampleTeacherDataSet().setPassword(encode);
            teacherService.joinTeacher(sampleTeacherDataSet());

            String encode2 = passwordEncoder.encode(sample2TeacherDataSet().getPassword());
            sample2TeacherDataSet().setPassword(encode2);
            teacherService.joinTeacher(sample2TeacherDataSet());

        }

    @Test
    @DisplayName("샘플 학생 정보")
    @Transactional
    @Rollback(value = false)
    void addStudentDataSet(){

        RegisterStudentDTO registerStudentDTO = new RegisterStudentDTO();
        Teacher testTeacher = teacherJpaRepository.findByUsernameIs("sampleTeacher");
        for (int i = 0; i < 4; i++) {
            registerStudentDTO.setStudentName("김샘플"+i);
            registerStudentDTO.setStudentPhoneNumber("010-1234-1234");
            registerStudentDTO.setStudentParentPhoneNumber("010-1234-1233");
            registerStudentDTO.setStudentAddress("대한민국 저기 어디");
            registerStudentDTO.setStudentDetailedAddress("어디");
            registerStudentDTO.setTeacherName(testTeacher.getTeacherName());
            Student entity = registerStudentDTO.toEntity();
            entity.setTeacherEntity(testTeacher);
            studentJpaRepository.save(entity);
        }
    }


        @Test
        @DisplayName("샘플 학생 수강 정보")
        void saveSampleStudent() throws InterruptedException {
            classService.saveClassTable(sampleClass());
        }


        @DisplayName("샘플 데이터 삭제")
        void deleteSampleData(){

        }
    }
