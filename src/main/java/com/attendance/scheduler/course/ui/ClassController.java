package com.attendance.scheduler.course.ui;

import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.course.dto.StudentClassRequest;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.ClassListResponse;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/class/")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;
    private final StudentService studentService;

    @PostMapping("findClass")
    public String findClass(@Validated @ModelAttribute("studentClassDTO") StudentClassRequest studentClassRequest,
                            @ModelAttribute("classDTO") ClassRequest classRequest, Model model) {

        boolean existStudentEntityByStudentName = studentService.existStudentEntityByStudentName(studentClassRequest.studentName());

        if (!existStudentEntityByStudentName) {
            model.addAttribute("nullStudentName", "등록 되지 않은 학생입니다.");
            return "index";
        }

        Optional<StudentClassResponse> studentClasses = classService.findStudentClasses(studentClassRequest.studentName());

        if (studentClasses.isPresent()) {
            searchStudentClass(studentClasses.get(), model);
            return "class/findClass";
        }

        getClassList(studentClassRequest.studentName(), model);
        return "class/findClass";
    }

    @PostMapping("submit")
    public String submitForm(@Validated @ModelAttribute("classDTO") ClassRequest classRequest, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            getClassList(classRequest.studentName(), model);
            log.info("errors={}", bindingResult);
            return "class/findClass";
        }

        try {
            classService.saveClassTable(classRequest);
            return "redirect:completion";
        } catch (Exception e) {
            Optional<StudentClassResponse> studentClasses = classService.findStudentClasses(classRequest.studentName());
            searchStudentClass(studentClasses.get(), model);
            model.addAttribute("error", e.getMessage());
            return "class/findClass";
        }
    }

    @GetMapping("completion")
    public String classCompletion() {
        return "class/completion";
    }

    private void getClassList(String studentName, Model model) {

        ClassListResponse allClasses = classService.findTeachersClasses(studentName);
        List<Integer> mondayClassList = allClasses.mondayClassList();
        List<Integer> tuesdayClassList = allClasses.tuesdayClassList();
        List<Integer> wednesdayClassList = allClasses.wednesdayClassList();
        List<Integer> thursdayClassList = allClasses.thursdayClassList();
        List<Integer> fridayClassList = allClasses.fridayClassList();

        model.addAttribute("studentClassList", new StudentClassResponse("", 0, 0, 0, 0, 0));

        model.addAttribute("studentName", studentName);
        model.addAttribute("classInMondayList", mondayClassList);
        model.addAttribute("classInTuesdayList", tuesdayClassList);
        model.addAttribute("classInWednesdayList", wednesdayClassList);
        model.addAttribute("classInThursdayList", thursdayClassList);
        model.addAttribute("classInFridayList", fridayClassList);
    }

    private void searchStudentClass(StudentClassResponse studentClassesList, Model model) {

        ClassListResponse allClasses = classService.findTeachersClasses(studentClassesList.studentName());

        List<Integer> mondayClassList = allClasses.mondayClassList();
        List<Integer> tuesdayClassList = allClasses.tuesdayClassList();
        List<Integer> wednesdayClassList = allClasses.wednesdayClassList();
        List<Integer> thursdayClassList = allClasses.thursdayClassList();
        List<Integer> fridayClassList = allClasses.fridayClassList();

        mondayClassList.remove(studentClassesList.monday());
        tuesdayClassList.remove(studentClassesList.tuesday());
        wednesdayClassList.remove(studentClassesList.wednesday());
        thursdayClassList.remove(studentClassesList.thursday());
        fridayClassList.remove(studentClassesList.friday());

        model.addAttribute("studentName", studentClassesList.studentName());

        model.addAttribute("classInMondayList", mondayClassList);
        model.addAttribute("classInTuesdayList", tuesdayClassList);
        model.addAttribute("classInWednesdayList", wednesdayClassList);
        model.addAttribute("classInThursdayList", thursdayClassList);
        model.addAttribute("classInFridayList", fridayClassList);

        model.addAttribute("studentClassList", studentClassesList);
    }
}
