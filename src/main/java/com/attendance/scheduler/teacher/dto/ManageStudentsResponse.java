package com.attendance.scheduler.teacher.dto;

import com.attendance.scheduler.student.dto.StudentInformationResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 수업관리 - 학생정보 목록 응답.
 * students: 페이징된 학생정보, teachers: 관리자에게만 채워지는 교사 목록(필터용).
 */
public record ManageStudentsResponse(
        List<StudentInformationResponse> students,
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<TeacherResponse> teachers
) {
    public static ManageStudentsResponse of(Page<StudentInformationResponse> page, List<TeacherResponse> teachers) {
        return new ManageStudentsResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                teachers
        );
    }
}
