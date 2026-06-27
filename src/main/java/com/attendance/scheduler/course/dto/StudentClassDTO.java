package com.attendance.scheduler.course.dto;

import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
@NoArgsConstructor
public class StudentClassDTO {

    //    학생 이름
    @NotEmpty(message = "학생 이름을 정확히 입력해 주세요")
    private String studentName;

    //    수업 시간
    private Integer monday;

    private Integer tuesday;

    private Integer wednesday;

    private Integer thursday;

    private Integer friday;

    private String teacherName;

    private Teacher teacherEntity;
}
