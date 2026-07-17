package com.attendance.scheduler.course.dto;

import java.util.List;

/**
 * 수강신청 폼에 필요한 데이터.
 * - periods: 선택 가능한 교시(1~6)
 * - current: 학생의 현재 신청 내역(없으면 모두 0)
 * - {day}Taken: 같은 선생님의 다른 학생이 이미 차지한 교시 목록(본인 교시는 제외) → 프론트에서 비활성화
 */
public record StudentClassFormResponse(
        String studentName,
        boolean hasClass,
        List<Integer> periods,
        StudentClassResponse current,
        List<Integer> mondayTaken,
        List<Integer> tuesdayTaken,
        List<Integer> wednesdayTaken,
        List<Integer> thursdayTaken,
        List<Integer> fridayTaken
) {
}
