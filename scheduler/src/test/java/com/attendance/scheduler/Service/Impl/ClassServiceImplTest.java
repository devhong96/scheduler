package com.attendance.scheduler.Service.Impl;

import com.attendance.scheduler.Config.Authority.UserDetailService;
import com.attendance.scheduler.Course.*;
import com.attendance.scheduler.Course.Dto.ClassDTO;
import com.attendance.scheduler.Course.Dto.StudentClassDTO;
import com.attendance.scheduler.Infra.Dto.LoginDTO;
import com.attendance.scheduler.Student.Dto.ClassListDTO;
import com.attendance.scheduler.Student.Dto.StudentInformationDTO;
import com.attendance.scheduler.Student.StudentService;
import com.attendance.scheduler.Teacher.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.attendance.scheduler.Config.TestDataSet.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ClassServiceImplTest {

    @Autowired private ClassService classService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudentService studentService;
    @Autowired private ClassTableRepository classTableRepository;
    @Autowired private ClassMapper classMapper;
    @Autowired private UserDetailService userDetailsService;

    private StudentClassDTO studentClassDTO;

    @BeforeEach
    void beforeEach(){

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

        StudentInformationDTO studentInformationDTO = new StudentInformationDTO();
        studentInformationDTO.setStudentName(testStudentClassDataSet().getStudentName());

        Optional<StudentInformationDTO> studentEntityByStudentName = studentService
                .findStudentEntityByStudentName(studentInformationDTO);

        if(studentEntityByStudentName.isEmpty()){
            studentService.registerStudentInformation(testStudentInformationDTO());
            classService.saveClassTable(testStudentClassDataSet());
        }

        studentInformationDTO.setStudentName(test2StudentClassDataSet().getStudentName());

        Optional<StudentInformationDTO> studentEntityByStudentName1 = studentService
                .findStudentEntityByStudentName(studentInformationDTO);

        if(studentEntityByStudentName1.isEmpty()){
            studentService.registerStudentInformation(test2StudentInformationDTO());
            classService.saveClassTable(test2StudentClassDataSet());
        }

        studentClassDTO = new StudentClassDTO();
        studentClassDTO.setStudentName(testStudentClassDataSet().getStudentName());
    }

    @Test
    @DisplayName("(무작위로 호출)값이 제대로 저장되었는지 확인")
    void checkClassList() {
        List<ClassDTO> dtoList = classTableRepository.findAll()
                .stream()
                .map(classMapper::toClassDTO)
                .toList();

        ClassListDTO classListDTO = ClassListDTO.getInstance();

        for (ClassDTO classDTO : dtoList) {
            classListDTO.getMondayClassList().add(classDTO.getMonday());
            classListDTO.getTuesdayClassList().add(classDTO.getTuesday());
            classListDTO.getWednesdayClassList().add(classDTO.getWednesday());
            classListDTO.getThursdayClassList().add(classDTO.getThursday());
            classListDTO.getFridayClassList().add(classDTO.getFriday());
        }

        for (int i = 0; i < dtoList.size(); i++) {
            assertEquals(classListDTO.getMondayClassList().get(i), dtoList.get(i).getMonday());
            assertEquals(classListDTO.getTuesdayClassList().get(i), dtoList.get(i).getTuesday());
            assertEquals(classListDTO.getWednesdayClassList().get(i), dtoList.get(i).getWednesday());
            assertEquals(classListDTO.getThursdayClassList().get(i), dtoList.get(i).getThursday());
            assertEquals(classListDTO.getFridayClassList().get(i), dtoList.get(i).getFriday());
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
                .findClassByStudent();

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
                .findAllClasses();
        //then
        assertEquals(2, allClasses.getMondayClassList().size());
    }

    @Test
    @DisplayName("학생 수업 조회")
    void findStudentClasses() {

        //when
        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO);

        //then
        studentClasses.ifPresent(classDTO ->
                assertThat(testStudentClassDataSet().getStudentName())
                        .isEqualTo(classDTO.getStudentName()));

    }

    @Test
    @DisplayName("수업 정보 저장")
    void saveClassTable() {

        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO);

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
    void modifyClass(){

        ClassDTO classDTO = testStudentClassDataSet();
        classDTO.setMonday(4);

        classService.saveClassTable(classDTO);

        //찾는 로직
        studentClassDTO = new StudentClassDTO();
        studentClassDTO.setStudentName(testStudentClassDataSet().getStudentName());

        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO);

        assertEquals(studentClasses.get().getMonday(), 4);
        assertEquals(studentClasses.get().getTuesday(),2);
        assertEquals(studentClasses.get().getWednesday(),3);
        assertEquals(studentClasses.get().getThursday(),4);
        assertEquals(studentClasses.get().getFriday(),5);
    }

    @Test
    @DisplayName("학생 수업 저장 상태 확인")
    void checkStudentClasses() {

        //When
        Optional<StudentClassDTO> studentClasses = classService
                .findStudentClasses(studentClassDTO);

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