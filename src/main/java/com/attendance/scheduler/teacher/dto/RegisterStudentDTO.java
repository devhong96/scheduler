package com.attendance.scheduler.teacher.dto;

import com.attendance.scheduler.student.domain.Student;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RegisterStudentDTO {

    @NotEmpty(message = "학생 이름을 정확히 입력해 주세요")
    private String studentName;

    @NotEmpty(message = "학생 전화번호를 입력해 주세요")
    private String studentPhoneNumber;

    @NotEmpty(message = "주소를 입력해 주세요")
    private String studentAddress;

    @NotEmpty(message = "상세주소를 입력해 주세요")
    private String studentDetailedAddress;

    @NotEmpty(message = "학부모 전화번호를 입력해 주세요")
    private String studentParentPhoneNumber;

    private String teacherName;

    private String teacherUsername;

    private Timestamp creationTimestamp;


    public void setStudentPhoneNumber(String studentPhoneNumber){
        this.studentPhoneNumber = studentPhoneNumber.replace("-", "");
    }

    public void setStudentParentPhoneNumber(String studentParentPhoneNumber){
        this.studentParentPhoneNumber = studentParentPhoneNumber.replace("-", "");
    }

    public Student toEntity(){
        return Student.builder()
                .studentName(studentName)
                .studentPhoneNumber(studentParentPhoneNumber)
                .studentParentPhoneNumber(studentPhoneNumber)
                .studentAddress(studentAddress)
                .studentDetailedAddress(studentDetailedAddress)
                .creationTimestamp(creationTimestamp)
                .build();
    }
}
