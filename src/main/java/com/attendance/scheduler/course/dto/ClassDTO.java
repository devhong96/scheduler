package com.attendance.scheduler.course.dto;

import com.attendance.scheduler.course.domain.Course;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter @Setter
@ToString
@NoArgsConstructor
public class ClassDTO {

    //    학생 이름
    private String studentName;

    //    수업 시간
    @NotNull(message = "요일을 선택해 주세요")
    private Integer monday;

    @NotNull(message = "요일을 선택해 주세요")
    private Integer tuesday;

    @NotNull(message = "요일을 선택해 주세요")
    private Integer wednesday;

    @NotNull(message = "요일을 선택해 주세요")
    private Integer thursday;

    @NotNull(message = "요일을 선택해 주세요")
    private Integer friday;

    private String teacherName;

    private Timestamp updateTimeStamp;

    public Course toEntity() {
        return Course.builder()
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .updateTimeStamp(updateTimeStamp)
                .build();
    }
}
