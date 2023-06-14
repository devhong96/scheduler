package com.attendance.scheduler.Controller;

import com.attendance.scheduler.Dto.ClassDTO;
import com.attendance.scheduler.Dto.ClassListDTO;
import com.attendance.scheduler.Dto.StudentClassDTO;
import com.attendance.scheduler.Service.SearchClassService;
import com.attendance.scheduler.Service.SubmitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/search/*")
@RequiredArgsConstructor
public class ClassSearchController {

    private final SubmitService submitService;
    private final SearchClassService searchClassService;

    //   수업 조회
    @PostMapping("findClass")
    public String findClass(@Validated @ModelAttribute("studentClass") StudentClassDTO studentClassDTO,
                            BindingResult bindingResult, Model model) {

        //학생 수업 조회
        StudentClassDTO studentClassesList = searchClassService.findStudentClasses(studentClassDTO);

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "search";
        }

        if (studentClassesList == null) {
            bindingResult.addError(new FieldError("studentClass", "nullStudentName", "등록되지 않은 이름입니다."));
            return "search";
        }

        ClassListDTO classesOrderByAsc = searchClassService.findClassesOrderByAsc();

        List<Integer> classInMondayList = classesOrderByAsc.getClassInMondayList();
        List<Integer> classInTuesdayList = classesOrderByAsc.getClassInTuesdayList();
        List<Integer> classInWednesdayList = classesOrderByAsc.getClassInWednesdayList();
        List<Integer> classInThursdayList = classesOrderByAsc.getClassInThursdayList();
        List<Integer> classInFridayList = classesOrderByAsc.getClassInFridayList();

        classInMondayList.remove(studentClassesList.getMonday());
        classInTuesdayList.remove(studentClassesList.getTuesday());
        classInWednesdayList.remove(studentClassesList.getWednesday());
        classInThursdayList.remove(studentClassesList.getThursday());
        classInFridayList.remove(studentClassesList.getFriday());

        model.addAttribute("classInMondayList", classInMondayList);
        model.addAttribute("classInTuesdayList", classInTuesdayList);
        model.addAttribute("classInWednesdayList", classInWednesdayList);
        model.addAttribute("classInThursdayList", classInThursdayList);
        model.addAttribute("classInFridayList", classInFridayList);

        log.info("monday = {}", classInMondayList);
        log.info("tuesday = {}", classInTuesdayList);
        log.info("wednesday = {}", classInWednesdayList);
        log.info("thursday = {}", classInThursdayList);
        log.info("friday = {}", classInFridayList);

        model.addAttribute("studentClass", new StudentClassDTO());
        model.addAttribute("studentClassList", studentClassesList);

        return "findClass";
    }

    //  수업 수정
    @PostMapping("modify")
    public String modifyClass(@Validated @ModelAttribute("class") ClassDTO classDTO,
                              BindingResult bindingResult) {


        log.info("studentInfo={}", classDTO.getStudentName());
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "findClass";
        }
        submitService.saveClassTable(classDTO);
        return "completion";
    }
}
