package com.attendance.scheduler.student.controller;

import com.attendance.scheduler.course.dto.StudentClassRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BasicController {

    @GetMapping("/")
    public String basic(@ModelAttribute("studentClassDTO") StudentClassRequest studentClassRequest) {
        return "index";
    }
}
