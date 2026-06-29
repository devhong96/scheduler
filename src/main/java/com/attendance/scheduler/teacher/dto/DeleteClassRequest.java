package com.attendance.scheduler.teacher.dto;

import java.util.List;

public record DeleteClassRequest(
        List<String> deleteClassList
) {
}
