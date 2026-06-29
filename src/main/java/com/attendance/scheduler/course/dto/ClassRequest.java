package com.attendance.scheduler.course.dto;

import com.attendance.scheduler.course.domain.Course;
import jakarta.validation.constraints.NotNull;

public record ClassRequest(
        String studentName,
        @NotNull(message = "요일을 선택해 주세요") Integer monday,
        @NotNull(message = "요일을 선택해 주세요") Integer tuesday,
        @NotNull(message = "요일을 선택해 주세요") Integer wednesday,
        @NotNull(message = "요일을 선택해 주세요") Integer thursday,
        @NotNull(message = "요일을 선택해 주세요") Integer friday
) {
    public ClassRequest withStudentName(String studentName) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public ClassRequest withMonday(Integer monday) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public ClassRequest withTuesday(Integer tuesday) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public ClassRequest withWednesday(Integer wednesday) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public ClassRequest withThursday(Integer thursday) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public ClassRequest withFriday(Integer friday) {
        return new ClassRequest(studentName, monday, tuesday, wednesday, thursday, friday);
    }

    public Course toEntity() {
        return Course.builder()
                .monday(monday)
                .tuesday(tuesday)
                .wednesday(wednesday)
                .thursday(thursday)
                .friday(friday)
                .build();
    }
}
