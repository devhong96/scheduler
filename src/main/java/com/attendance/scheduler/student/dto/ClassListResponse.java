package com.attendance.scheduler.student.dto;

import java.util.ArrayList;
import java.util.List;

public record ClassListResponse(
        String studentName,
        List<Integer> mondayClassList,
        List<Integer> tuesdayClassList,
        List<Integer> wednesdayClassList,
        List<Integer> thursdayClassList,
        List<Integer> fridayClassList
) {
    public static ClassListResponse getInstance() {
        return new ClassListResponse("", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public ClassListResponse withStudentName(String studentName) {
        return new ClassListResponse(studentName, mondayClassList, tuesdayClassList, wednesdayClassList, thursdayClassList, fridayClassList);
    }
}
