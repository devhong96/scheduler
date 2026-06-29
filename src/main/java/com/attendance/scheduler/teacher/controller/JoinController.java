package com.attendance.scheduler.teacher.controller;

import com.attendance.scheduler.teacher.application.TeacherService;
import com.attendance.scheduler.teacher.dto.JoinTeacherRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/join/")
@RequiredArgsConstructor
public class JoinController {

    private final TeacherService teacherService;

    //회원가입 폼
    @GetMapping("teacher")
    public String joinForm(Model model) {
        // 빈 폼 표시용 기본 객체. @ModelAttribute 파라미터로 받으면 primitive 필드(approved)가 null 바인딩되어 400이 발생한다.
        model.addAttribute("join", new JoinTeacherRequest("", "", "", "", false));
        return "join";
    }

    //회원가입 완료
    @PostMapping("approved")
    public String approved(@Validated @ModelAttribute("join") JoinTeacherRequest joinTeacherDTO, BindingResult bindingResult,
                           Model model) {

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "join";
        }

        boolean duplicateTeacherId = teacherService
                .findDuplicateTeacherID(joinTeacherDTO);

        if (duplicateTeacherId) {
            model.addAttribute("idErrorMessage", "이미 가입된 아이디 입니다.");
            return "join";
        }

        boolean duplicateTeacherEmail = teacherService
                .findDuplicateTeacherEmail(joinTeacherDTO);

        if (duplicateTeacherEmail) {

            model.addAttribute("emailErrorMessage", "이미 가입된 이메일 입니다.");
            return "join";
        }

        try {
            teacherService.joinTeacher(joinTeacherDTO);
            return "redirect:/login";
        } catch (Exception e) {
            e.getStackTrace();
            return "join";
        }
    }
}
