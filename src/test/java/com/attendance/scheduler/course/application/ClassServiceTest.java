package com.attendance.scheduler.course.application;
import org.springframework.test.context.ActiveProfiles;

import com.attendance.scheduler.common.dto.LoginRequest;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.course.dto.ClassResponse;
import com.attendance.scheduler.course.dto.StudentClassRequest;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.ClassListResponse;
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

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ClassServiceTest {

    @Autowired private ClassService classService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudentService studentService;
    @Autowired private ClassRepository classRepository;
    @Autowired private UserDetailsService userDetailsService;


    private StudentClassRequest studentClassDTO;

    @BeforeEach
    void beforeEach() throws InterruptedException {

        boolean duplicateTeacherID = teacherService
                .findDuplicateTeacherID(testTeacherDataSet());

        if (!duplicateTeacherID) {
            teacherService.joinTeacher(testTeacherDataSet());
        }

        //Given, 교사 로그인
        LoginRequest loginDTO = new LoginRequest(testTeacherDataSet().username(), testTeacherDataSet().password());

        UserDetails userDetails = userDetailsService
                .loadUserByUsername(loginDTO.username());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testTeacherDataSet().username(),
                        testTeacherDataSet().password() , userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);


        //테스트 학생 등록
        boolean studentEntityByStudentName = studentService
                .existStudentEntityByStudentName(testStudentClassDataSet().studentName());

        if(!studentEntityByStudentName){
            teacherService.registerStudentInformation(testStudentInformationDTO());
            classService.saveClassTable(testStudentClassDataSet());
        }

        boolean studentEntityByStudentName1 = studentService
                .existStudentEntityByStudentName(test2StudentClassDataSet().studentName());
        //테스트 학생1 등록
        if(!studentEntityByStudentName1){
            teacherService.registerStudentInformation(test2StudentInformationDTO());
            classService.saveClassTable(test2StudentClassDataSet());
        }

        studentClassDTO = new StudentClassRequest(testStudentClassDataSet().studentName());
    }

    @Test
    @DisplayName("(무작위로 호출)값이 제대로 저장되었는지 확인")
    void checkClassList() {

        ClassListResponse classListDTO = ClassListResponse.getInstance();
        List<ClassResponse> studentClassList = classRepository.getStudentClassList();

        for (ClassResponse classDTO : studentClassList) {
            classListDTO.mondayClassList().add(classDTO.monday());
            classListDTO.tuesdayClassList().add(classDTO.tuesday());
            classListDTO.wednesdayClassList().add(classDTO.wednesday());
            classListDTO.thursdayClassList().add(classDTO.thursday());
            classListDTO.fridayClassList().add(classDTO.friday());
        }

        for (int i = 0; i < studentClassList.size(); i++) {
            assertEquals(classListDTO.mondayClassList().get(i), studentClassList.get(i).monday());
            assertEquals(classListDTO.tuesdayClassList().get(i), studentClassList.get(i).tuesday());
            assertEquals(classListDTO.wednesdayClassList().get(i), studentClassList.get(i).wednesday());
            assertEquals(classListDTO.thursdayClassList().get(i), studentClassList.get(i).thursday());
            assertEquals(classListDTO.fridayClassList().get(i), studentClassList.get(i).friday());
        }
    }

    @Test
    @DisplayName("(리스트 별로 호출)저장된 수강 정보를 학생 별 확인")
    void findClassByStudent() {

        //Given
        List<ClassRequest> classList = Arrays
                .asList(testStudentClassDataSet(), test2StudentClassDataSet());

        //when
        List<ClassResponse> classByStudent = classService
                .findStudentClassList();

        //then
        for (int i = 0; i < classByStudent.size(); i++) {
            assertEquals(classList.get(i).monday(), classByStudent.get(i).monday());
            assertEquals(classList.get(i).tuesday(), classByStudent.get(i).tuesday());
            assertEquals(classList.get(i).wednesday(), classByStudent.get(i).wednesday());
            assertEquals(classList.get(i).thursday(), classByStudent.get(i).thursday());
            assertEquals(classList.get(i).friday(), classByStudent.get(i).friday());
        }
    }

    @Test
    @DisplayName("저장된 수강 정보 확인")
    void findAllClasses() {
        //when
        ClassListResponse allClasses = classService
                .findTeachersClasses(test2StudentClassDataSet().studentName());
        //then
        assertEquals(2, allClasses.mondayClassList().size());
    }

    @Test
    @DisplayName("학생 수업 조회")
    void findStudentClasses() {

        //when
        Optional<StudentClassResponse> studentClasses = classService
                .findStudentClasses(studentClassDTO.studentName());

        //then
        studentClasses.ifPresent(classDTO ->
                assertThat(testStudentClassDataSet().studentName())
                        .isEqualTo(classDTO.studentName()));
    }

    @Test
    @DisplayName("수업 정보 저장")
    void saveClassTable() {

        Optional<StudentClassResponse> studentClasses = classService
                .findStudentClasses(studentClassDTO.studentName());

        studentClasses.ifPresent(classDTO -> {
            assertThat(classDTO.studentName())
                    .isEqualTo(testStudentClassDataSet().studentName());

            assertThat(classDTO.monday())
                    .isEqualTo(testStudentClassDataSet().monday());

            assertThat(classDTO.tuesday())
                    .isEqualTo(testStudentClassDataSet().tuesday());

            assertThat(classDTO.wednesday())
                    .isEqualTo(testStudentClassDataSet().wednesday());

            assertThat(classDTO.thursday())
                    .isEqualTo(testStudentClassDataSet().thursday());

            assertThat(classDTO.monday())
                    .isEqualTo(testStudentClassDataSet().monday());
        });
    }

    @Test
    @DisplayName("수업 변경 확인")
    void modifyClass() throws InterruptedException {

        //찾는 로직
        ClassRequest classRequest = testStudentClassDataSet();
        classRequest = classRequest.withMonday(4);

        classService.saveClassTable(classRequest);

        studentClassDTO = new StudentClassRequest(testStudentClassDataSet().studentName());

        Optional<StudentClassResponse> studentClasses = classService
                .findStudentClasses(studentClassDTO.studentName());

        studentClasses.ifPresent(ClassList -> {
            assertThat(ClassList.studentName())
                    .isEqualTo(testStudentClassDataSet().studentName());

            assertThat(ClassList.monday())
                    .isEqualTo(4);

            assertThat(ClassList.tuesday())
                    .isEqualTo(testStudentClassDataSet().tuesday());

            assertThat(ClassList.wednesday())
                    .isEqualTo(testStudentClassDataSet().wednesday());

            assertThat(ClassList.thursday())
                    .isEqualTo(testStudentClassDataSet().thursday());

            assertThat(ClassList.friday())
                    .isEqualTo(testStudentClassDataSet().friday());
        });
    }

    @Test
    @DisplayName("학생 수업 저장 상태 확인")
    void checkStudentClasses() {

        //When
        Optional<StudentClassResponse> studentClasses = classService
                .findStudentClasses(studentClassDTO.studentName());

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
