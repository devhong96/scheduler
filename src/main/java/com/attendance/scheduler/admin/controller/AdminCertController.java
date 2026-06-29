package com.attendance.scheduler.admin.controller;

import com.attendance.scheduler.admin.application.AdminCertService;
import com.attendance.scheduler.admin.application.AdminService;
import com.attendance.scheduler.admin.dto.EditEmailRequest;
import com.attendance.scheduler.admin.dto.EmailResponse;
import com.attendance.scheduler.teacher.dto.PwdEditRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/help/")
public class AdminCertController {

    public final AdminService adminService;
    public final AdminCertService adminCertService;

    @GetMapping("password")
    public String changePassword(@ModelAttribute("pwdEdit") PwdEditRequest pwdEditDTO) {
        return "admin/help/changePassword";
    }

    @PostMapping("password")
    public String authCompletion(@Valid PwdEditRequest pwdEditDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        pwdEditDTO = pwdEditDTO.withUsername(auth.getName());
        try {
            adminCertService.initializePassword(pwdEditDTO);
            return "redirect:/help/completion";
        } catch (Exception e) {
            return "redirect:/";
        }
    }

    @GetMapping("email")
    public String changeEmail(@ModelAttribute("emailEdit") EditEmailRequest editEmailDTO, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        EmailResponse emailDTO = new EmailResponse(auth.getName(), "");

        if (adminService.findAdminEmailByID(emailDTO).isPresent()) {
            emailDTO = emailDTO.withEmail(adminService.findAdminEmailByID(emailDTO).get().email());
        }

        model.addAttribute("username", emailDTO);
        return "admin/help/changeEmail";
    }

    @PostMapping("email")
    public String updateEmail(EditEmailRequest editEmailDTO) {
        try {
            adminCertService.updateEmail(editEmailDTO);
            return "redirect:cert/completion";
        } catch (Exception e) {
            return "manage/class";
        }
    }
}
