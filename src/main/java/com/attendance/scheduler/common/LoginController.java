package com.attendance.scheduler.common;

import com.attendance.scheduler.common.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    @GetMapping(value = "/login")
    public String teacherLoginForm(@ModelAttribute("login") LoginRequest loginDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UserDetails) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/login/error")
    public String teacherLogin(@ModelAttribute("login") LoginRequest loginDTO, HttpSession session, Model model){
        log.info("errorMessage = {}", session.getAttribute("errorMessage"));
        model.addAttribute("errorMessage", session.getAttribute("errorMessage"));
        return "login";
    }
}
