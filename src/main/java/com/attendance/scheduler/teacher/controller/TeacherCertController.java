package com.attendance.scheduler.teacher.controller;

import com.attendance.scheduler.infra.auth.AuthCodeService;
import com.attendance.scheduler.infra.auth.AuthVerification;
import com.attendance.scheduler.infra.email.EmailService;
import com.attendance.scheduler.infra.email.FindPasswordRequest;
import com.attendance.scheduler.teacher.application.TeacherCertService;
import com.attendance.scheduler.teacher.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/help/")
@RequiredArgsConstructor
public class TeacherCertController {

    public final TeacherCertService teacherCertService;
    public final EmailService emailService;
    public final AuthCodeService authCodeService;

    @PostMapping("sendUserId")
    public String sendEmail(@Validated @ModelAttribute("account") FindIdRequest findIdRequest,
                            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/help/findId";
        }

        Optional<FindIdResponse> idByEmail = teacherCertService.findIdByEmail(findIdRequest);
        log.info("email={}", idByEmail);

        if (idByEmail.isEmpty()) {
            model.addAttribute("errorMessage", "등록된 이메일이 없습니다.");
            return "member/help/findId";
        }

        try {
            emailService.sendUserId(idByEmail.get());
        } catch (Exception e) {
            log.info("send Id error = {}", e.getMessage());
        }

        return "member/help/idCompletion";
    }

    @PostMapping("findPwd")
    public String idEmailConfirm(@Validated @ModelAttribute("account") FindPasswordRequest findPasswordDTO,
                                 BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "member/help/findPwd";
        }

        if (!teacherCertService.emailConfirmation(findPasswordDTO)) {
            model.addAttribute("errorMessage", "등록된 이메일이 없습니다.");
            return "member/help/findPwd";
        }

        if (!teacherCertService.idConfirmation(findPasswordDTO)) {
            model.addAttribute("errorMessage", "등록된 아이디가 없습니다.");
            return "member/help/findPwd";
        }

        try {
            String authCode = authCodeService.issue(findPasswordDTO.username());
            emailService.sendAuthCode(findPasswordDTO.email(), authCode);
            model.addAttribute("username", findPasswordDTO.username());
            model.addAttribute("auth", new CertRequest(findPasswordDTO.username(), ""));
            return "member/help/authNum";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/help/findPwd";
        }
    }

    @PostMapping("authNumCheck")
    public String authNumCheck(Model model, CertRequest certDTO) {
        log.info("CertDTO={}", certDTO);

        AuthVerification result =
                authCodeService.verify(certDTO.username(), certDTO.authNum());

        if (result == AuthVerification.SUCCESS) {
            model.addAttribute("pwdEdit", new PwdEditRequest(certDTO.username(), ""));
            model.addAttribute("username", certDTO);
            return "member/help/initializePassword";
        }

        model.addAttribute("auth", new CertRequest(certDTO.username(), ""));
        model.addAttribute("username", certDTO);
        model.addAttribute("errorMessage", switch (result) {
            case NOT_ISSUED -> "인증번호를 전송해주세요";
            case EXPIRED -> "인증시간이 만료되었습니다";
            default -> "인증번호가 일치하지 않습니다";
        });
        return "member/help/authNum";
    }

    @GetMapping("password")
    public String changePassword(@ModelAttribute("pwdEdit") PwdEditRequest pwdEditDTO) {
        return "member/help/changePassword";
    }

    @PostMapping("password")
    public String authCompletion(PwdEditRequest pwdEditDTO) {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!auth.getName().equals("anonymousUser")) {
            pwdEditDTO = pwdEditDTO.withUsername(auth.getName());
        }

        try {
            teacherCertService.initializePassword(pwdEditDTO);
            return "redirect:/help/completion";
        } catch (Exception e) {
            log.info("error = {}", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("email")
    public String changeEmail(@ModelAttribute("emailEdit") EditEmailRequest editEmailDTO, Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        FindIdRequest findIdRequest = new FindIdRequest("");
        Optional<FindIdResponse> idByEmail = teacherCertService.findIdByEmail(findIdRequest);
        FindIdResponse findIdResponse = idByEmail
                .orElse(new FindIdResponse("", username));

        model.addAttribute("username", findIdResponse);
        return "member/help/changeEmail";
    }

    @PostMapping("email")
    public String updateEmail(EditEmailRequest editEmailDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        editEmailDTO = editEmailDTO.withUsername(auth.getName());

        try {
            teacherCertService.updateEmail(editEmailDTO);
            return "redirect:/help/completion";
        } catch (Exception e) {
            return "manage/class";
        }
    }
}
