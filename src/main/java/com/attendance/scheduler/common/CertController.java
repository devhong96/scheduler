package com.attendance.scheduler.common;

import com.attendance.scheduler.infra.email.FindPasswordRequest;
import com.attendance.scheduler.teacher.dto.FindIdRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/help/")
public class CertController {

    @GetMapping("findId")
    public String findId(@ModelAttribute("account") FindIdRequest findIdRequest) {
        return "member/help/findId";
    }

    @GetMapping("findPassword")
    public String findPassword(@ModelAttribute("account") FindPasswordRequest findPasswordDTO) {
        return "member/help/findPwd";
    }

    @GetMapping("completion")
    public String updateCompletionForm() {
        return "member/help/completion";
    }
}
