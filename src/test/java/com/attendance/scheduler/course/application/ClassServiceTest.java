package com.attendance.scheduler.course.application;

import com.attendance.scheduler.common.dto.LoginDTO;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.ClassListDTO;
import com.attendance.scheduler.teacher.application.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.attendance.scheduler.testDataSet.TestDataSet.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClassServiceTest {

    @Autowired private ClassService classService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudentService studentService;
    @Autowired private ClassRepository classRepository;
    @Autowired private UserDetailsService userDetailsService;


    private StudentClassDTO studentClassDTO;

    @BeforeEach
    void beforeEach() throws InterruptedException {

        boolean duplicateTeacherID = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        if (!duplicateTeacherID) {
            teacherService.joinTeacher(testTeacherDataSet());
        }

        //Given, 교사 로그인
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername(testTeacherDataSet().getUsername());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDTO.getUsername());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testTeacherDataSet().getUsername(),
                        testTeacherDataSet().getPassword() , userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);


        //테스트 학생 등록
        boolean studentEntityByStudentName = studentService
                .existStudentEntityByStudentName(testStudentClassDataSet().getStudentName());

        if(!studentEntityByStudentName){
            teacherService.registerStudentInformation(testStudentInformationDTO());
            classService.saveClassTable(testStudentClassDataSet());
        }

        boolean studentEntityByStudentName1 = studentService
                .existStudentEntityByStudentName(test2StudentClassDataSet().getStudentName());
        //테스트 학생1 등록
        if(!studentEntityByStudentName1){
            teacherService.registerStudentInformation(test2StudentInformationDTO());
            classService.saveClassTable(test2StudentClassDataSet());
        }

        studentClassDTO = new StudentClassDTO();
        studentClassDTO.setStudentName(testStudentClassDataSet().getStudentName());
    }

    @Test
    @DisplayName("(무작위로 호출)값이 제대로 저장되었는지 확인")
    void checkClassList() {

        ClassListDTO classListDTO = ClassListDTO.getInstance();
        List<ClassDTO> studentClassList = classRepository.getStudentClassList();

        for (ClassDTO classDTO : studentClassList) {
            classListDTO.getMondayClassList().add(classDTO.getMonday());
            classListDTO.getTuesdayClassList().add(classDTO.getTuesday());
            classListDTO.getWednesdayClassList().add(classDTO.getWednesday());
            classListDTO.getThursdayClassList().add(classDTO.getThursday());
            classListDTO.getFridayClassList().add(classDTO.getFriday());
        }

        for (int i = 0; i < studentClassList.size(); i++) {
            assertEquals(classListDTO.getMondayClassList().get(i), studentClassList.get(i).getMonday());
            assertEquals(classListDTO.getTuesdayClassList().get(i), studentClassList.get(i).getTuesday());
            assertEquals(classListDTO.getWednesdayClassList().get(i), studentClassList.get(i).getWednesday());
            assertEquals(classListDTO.getThursdayClassList().get(i), studentClassList.get(i).getThursday());
            assertEquals(classListDTO.getFridayClassList().get(i), studentClassList.get(i).getFriday());
        }
    }

    @Test
    @DisplayName("(리스트 별로 호출)저장된 수강 정보를 학생 별 확인")
    void findClassByStudent() {

        //Given
        List<ClassDTO> classList = Arrays
                .asList(testStudentClassDataSet(), test2StudentClassDataSet());

        //when
        List<ClassDTO> classByStudent = classService
                .findStudentClassList();

        //then
        for (int i = 0; i < classByStudent.size(); i++) {
            assertEquals(classList.get(i).getMonday(), classByStudent.get(i).getMonday());
            assertEquals(classList.get(i).getTuesday(), classByStudent.get(i).getTuesday());
            assertEquals(classList.get(i).getWednesday(), classByStudent.get(i).getWednesday());
            assertEquals(classList.get(i).getThursday(), classByStudent.get(i).getThursday());
            assertEquals(classList.get(i).getFriday(), classByStudent.get(i).getFriday());
        }
    }

    @Test
    @DisplayName("저장된 수강 정보 확인")
    void findAllClasses() {
        //when
        ClassListDTO allClasses = classService
                .findTeachersClasses(test2StudentClassDataSet().getStudentName());
        //then
        assertEquals(2, allClasses.getMondayClassList().size());
    }

    @Test
    @DisplayName("학생 수업 조회")
    void findStudentClasses() {

        //when
        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO.getStudentName());

        //then
        studentClasses.ifPresent(classDTO ->
                assertThat(testStudentClassDataSet().getStudentName())
                        .isEqualTo(classDTO.getStudentName()));
    }

    @Test
    @DisplayName("수업 정보 저장")
    void saveClassTable() {

        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO.getStudentName());

        studentClasses.ifPresent(classDTO -> {
            assertThat(classDTO.getStudentName())
                    .isEqualTo(testStudentClassDataSet().getStudentName());

            assertThat(classDTO.getMonday())
                    .isEqualTo(testStudentClassDataSet().getMonday());

            assertThat(classDTO.getTuesday())
                    .isEqualTo(testStudentClassDataSet().getTuesday());

            assertThat(classDTO.getWednesday())
                    .isEqualTo(testStudentClassDataSet().getWednesday());

            assertThat(classDTO.getThursday())
                    .isEqualTo(testStudentClassDataSet().getThursday());

            assertThat(classDTO.getMonday())
                    .isEqualTo(testStudentClassDataSet().getMonday());
        });
    }

    @Test
    @DisplayName("수업 변경 확인")
    void modifyClass() throws InterruptedException {

        //찾는 로직
        ClassDTO classDTO = testStudentClassDataSet();
        classDTO.setMonday(4);

        classService.saveClassTable(classDTO);

        studentClassDTO = new StudentClassDTO();
        studentClassDTO.setStudentName(testStudentClassDataSet().getStudentName());

        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO.getStudentName());

        studentClasses.ifPresent(ClassList -> {
            assertThat(ClassList.getStudentName())
                    .isEqualTo(testStudentClassDataSet().getStudentName());

            assertThat(ClassList.getMonday())
                    .isEqualTo(4);

            assertThat(ClassList.getTuesday())
                    .isEqualTo(testStudentClassDataSet().getTuesday());

            assertThat(ClassList.getWednesday())
                    .isEqualTo(testStudentClassDataSet().getWednesday());

            assertThat(ClassList.getThursday())
                    .isEqualTo(testStudentClassDataSet().getThursday());

            assertThat(ClassList.getFriday())
                    .isEqualTo(testStudentClassDataSet().getFriday());
        });
    }

    @Test
    @DisplayName("학생 수업 저장 상태 확인")
    void checkStudentClasses() {

        //When
        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO.getStudentName());

        //Then
        boolean present = studentClasses.isPresent();
        assertTrue(present);
    }

    @Test
    @DisplayName("중복 수업 확인")
    void duplicatedClassTest() {

        //when //then
        assertThrows(IllegalStateException.class,
                () -> classService.saveClassTable(testStudent_duplicated()));
    }
}