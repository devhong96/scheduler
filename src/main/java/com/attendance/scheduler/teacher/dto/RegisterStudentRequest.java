package com.attendance.scheduler.teacher.dto;

import com.attendance.scheduler.student.domain.Student;
import jakarta.validation.constraints.NotEmpty;

public record RegisterStudentRequest(
        @NotEmpty(message = "학생 이름을 정확히 입력해 주세요") String studentName,
        @NotEmpty(message = "학생 전화번호를 입력해 주세요") String studentPhoneNumber,
        @NotEmpty(message = "주소를 입력해 주세요") String studentAddress,
        @NotEmpty(message = "상세주소를 입력해 주세요") String studentDetailedAddress,
        @NotEmpty(message = "학부모 전화번호를 입력해 주세요") String studentParentPhoneNumber,
        String teacherName,
        String teacherUsername
) {
    public RegisterStudentRequest withStudentName(String studentName) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public RegisterStudentRequest withStudentPhoneNumber(String studentPhoneNumber) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber.replace("-", ""), studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public RegisterStudentRequest withStudentParentPhoneNumber(String studentParentPhoneNumber) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber.replace("-", ""), teacherName, teacherUsername);
    }

    public RegisterStudentRequest withStudentAddress(String studentAddress) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public RegisterStudentRequest withStudentDetailedAddress(String studentDetailedAddress) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public RegisterStudentRequest withTeacherName(String teacherName) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public RegisterStudentRequest withTeacherUsername(String teacherUsername) {
        return new RegisterStudentRequest(studentName, studentPhoneNumber, studentAddress, studentDetailedAddress, studentParentPhoneNumber, teacherName, teacherUsername);
    }

    public Student toEntity() {
        return Student.builder()
                .studentName(studentName)
                .studentPhoneNumber(studentParentPhoneNumber)
                .studentParentPhoneNumber(studentPhoneNumber)
                .studentAddress(studentAddress)
                .studentDetailedAddress(studentDetailedAddress)
                .build();
    }
}
