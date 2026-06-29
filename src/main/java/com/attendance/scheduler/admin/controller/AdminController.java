package com.attendance.scheduler.admin.controller;

import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.teacher.dto.TeacherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    public final AdminService adminService;

    @GetMapping("/teacherList")
    public String teacherList(Model model) {
        List<TeacherResponse> teacherList = adminService.getTeacherList();
        model.addAttribute("teacherList", teacherList);
        return "admin/teacherList";
    }
}
