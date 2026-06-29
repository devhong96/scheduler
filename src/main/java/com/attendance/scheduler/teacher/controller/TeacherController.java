package com.attendance.scheduler.teacher.controller;

import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassResponse;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.StudentInformationRequest;
import com.attendance.scheduler.student.dto.StudentInformationResponse;
import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.RegisterStudentRequest;
import com.attendance.scheduler.teacher.dto.StudentSearchCondition;
import com.attendance.scheduler.teacher.dto.TeacherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/manage/")
@RequiredArgsConstructor
public class TeacherController {

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final ClassService classService;
    private final AdminService adminService;

    @GetMapping("class")
    public String managePage(Model model) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<ClassResponse> classTable = classService.findStudentClassList();

        if (auth.getAuthorities().toString().equals("[ROLE_TEACHER]")) {
            List<TeacherResponse> teacherInformation = adminService.findTeacherInformation(auth.getName());
            List<ClassResponse> collect = classTable
                    .stream().filter(h -> h.teacherName()
                            .equals(teacherInformation.get(0).teacherName()))
                    .collect(Collectors.toList());
            model.addAttribute("findClassTable", collect);
            return "manage/class";
        }

        model.addAttribute("findClassTable", classTable);
        return "manage/class";
    }

    @PostMapping("delete")
    public ResponseEntity<String> deleteSchedule(String studentName) {
        classService.deleteClass(studentName);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    @GetMapping("studentList")
    public String studentList(StudentSearchCondition studentSearchCondition, Pageable pageable, Model model) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Page<StudentInformationResponse> studentInformationList = teacherService
                .findStudentInformationList(studentSearchCondition, pageable);
        model.addAttribute("maxPage", 5);

        if (auth.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            List<TeacherResponse> teacherList = adminService.getTeacherList();
            model.addAttribute("teacherList", teacherList);
            model.addAttribute("studentList", studentInformationList);
            return "manage/studentList";
        }

        model.addAttribute("studentList", studentInformationList);
        return "manage/studentList";
    }

    @GetMapping("registerStudentInformation")
    public String addStudentInformation(@ModelAttribute("studentObject") RegisterStudentRequest registerStudentDTO) {
        return "manage/registerStudentInformation";
    }

    @PostMapping("saveStudentList")
    public String saveStudentList(@Validated @ModelAttribute("studentObject") RegisterStudentRequest registerStudentDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "manage/registerStudentInformation";
        }

        boolean studentEntityByStudentName = studentService.existStudentEntityByStudentName(registerStudentDTO.studentName());

        if (studentEntityByStudentName) {
            model.addAttribute("studentInformation", "이미 등록된 학생의 이름입니다.");
            return "manage/registerStudentInformation";
        }

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        registerStudentDTO = registerStudentDTO.withTeacherUsername(auth.getName());

        try {
            teacherService.registerStudentInformation(registerStudentDTO);
            return "redirect:/manage/studentList";
        } catch (Exception e) {
            return "manage/registerStudentInformation";
        }
    }

    @PostMapping("deleteStudentList")
    public ResponseEntity<String> deleteStudentList(@Validated @ModelAttribute StudentInformationRequest studentInformationRequest) {
        teacherService.deleteStudentInformation(studentInformationRequest);
        return ResponseEntity.ok("삭제되었습니다.");
    }
}
