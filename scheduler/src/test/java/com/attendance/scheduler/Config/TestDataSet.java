package com.attendance.scheduler.Config;

import com.attendance.scheduler.Dto.ClassDTO;
import com.attendance.scheduler.Dto.StudentInformationDTO;
import com.attendance.scheduler.Dto.Teacher.JoinTeacherDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestDataSet {

    public static ClassDTO testStudent(){
        ClassDTO classDTO = new ClassDTO();
        classDTO.setStudentName("testStudent");
        classDTO.setMonday(1);
        classDTO.setTuesday(2);
        classDTO.setWednesday(3);
        classDTO.setThursday(4);
        classDTO.setFriday(5);
        return classDTO;
    }

    public static ClassDTO test2Student(){
        ClassDTO classDTO = new ClassDTO();
        classDTO.setStudentName("test2Student");
        classDTO.setMonday(2);
        classDTO.setTuesday(3);
        classDTO.setWednesday(4);
        classDTO.setThursday(5);
        classDTO.setFriday(6);
        return classDTO;
    }

    public static ClassDTO testStudent_duplicated(){
        ClassDTO classDTO = new ClassDTO();
        classDTO.setStudentName("test3Student");
        classDTO.setMonday(1);
        classDTO.setTuesday(2);
        classDTO.setWednesday(3);
        classDTO.setThursday(4);
        classDTO.setFriday(5);
        return classDTO;
    }

    public static JoinTeacherDTO sampleTeacherDataSet(){
        JoinTeacherDTO joinTeacherDTO = new JoinTeacherDTO();
        joinTeacherDTO.setUsername("testTeacher");
        joinTeacherDTO.setPassword("123");
        joinTeacherDTO.setEmail("testTeacherDataSet@gmail.com");
        joinTeacherDTO.setName("김교사");
        joinTeacherDTO.setApproved(true);
        return joinTeacherDTO;
    }

    public static StudentInformationDTO sampleStudentInformationDTO(){
        StudentInformationDTO studentInformationDTO = new StudentInformationDTO();
        studentInformationDTO.setStudentName("김학생");
        studentInformationDTO.setStudentPhoneNumber("010-1234-1234");
        studentInformationDTO.setStudentParentPhoneNumber("010-1234-1233");
        studentInformationDTO.setStudentAddress("대한민국 저기 어디");
        studentInformationDTO.setStudentDetailedAddress("어디");
        return studentInformationDTO;

    }

    public static StudentInformationDTO sample2StudentInformationDTO(){
        StudentInformationDTO studentInformationDTO = new StudentInformationDTO();
        studentInformationDTO.setStudentName("박학생");
        studentInformationDTO.setStudentPhoneNumber("010-4321-4321");
        studentInformationDTO.setStudentParentPhoneNumber("010-4321-4322");
        studentInformationDTO.setStudentAddress("대한민국 저기 먼데");
        studentInformationDTO.setStudentDetailedAddress("먼데");
        return studentInformationDTO;

    }

}
