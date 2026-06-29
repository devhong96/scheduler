package com.attendance.scheduler.testDataSet;

import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import com.attendance.scheduler.teacher.dto.RegisterStudentRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestDataSet {

    public static ClassRequest testStudentClassDataSet() {
        return new ClassRequest("김학생", 1, 2, 3, 4, 5);
    }

    public static ClassRequest test2StudentClassDataSet() {
        return new ClassRequest("이학생", 2, 3, 4, 5, 6);
    }

    public static ClassRequest testStudent_duplicated() {
        return new ClassRequest("박학생", 1, 2, 3, 4, 5);
    }

    public static JoinTeacherRequest testTeacherDataSet(){
        return new JoinTeacherRequest("testTeacher", "123", "김교사", "testTeacherDataSet@gmail.com", true);
    }

    public static RegisterStudentRequest testStudentInformationDTO(){
        return new RegisterStudentRequest("김학생", "01012341234", "대한민국 저기 어디", "어디",
                "01012341233", "", "testTeacher");
    }

    public static RegisterStudentRequest test2StudentInformationDTO(){
        return new RegisterStudentRequest("이학생", "01043214321", "대한민국 저기 먼데", "먼데",
                "01043214322", "", "testTeacher");
    }

}
