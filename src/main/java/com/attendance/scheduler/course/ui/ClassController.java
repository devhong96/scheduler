package com.attendance.scheduler.course.ui;

import com.attendance.scheduler.course.application.ClassService;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.student.application.StudentService;
import com.attendance.scheduler.student.dto.ClassListDTO;
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

    //   수업 조회
    @PostMapping("findClass")
    public String findClass(@Validated @ModelAttribute("studentClassDTO") StudentClassDTO studentClassDTO, Model model) {

        boolean existStudentEntityByStudentName = studentService.existStudentEntityByStudentName(studentClassDTO.getStudentName());

        if(!existStudentEntityByStudentName) {
            model.addAttribute("studentClassDTO", new StudentClassDTO());
            model.addAttribute("nullStudentName", "등록 되지 않은 학생입니다.");
            return "index";
        }

        Optional<StudentClassDTO> studentClasses = classService.findStudentClasses(studentClassDTO.getStudentName());

        if(studentClasses.isPresent()) {
            searchStudentClass(studentClasses.get(), model);
            return "class/findClass";
        }

        getClassList(studentClassDTO.getStudentName(), model);
        return "class/findClass";
    }

    //제출
    @PostMapping("submit")
    public String submitForm(@Validated @ModelAttribute("classDTO") ClassDTO classDTO, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            getClassList(classDTO.getStudentName(), model);
            log.info("errors={}", bindingResult);
            return "class/findClass";
        }

        try {
            classService.saveClassTable(classDTO);
            return "redirect:completion";
        }catch (Exception e){

            StudentClassDTO studentClassesList = new StudentClassDTO();
            studentClassesList.setStudentName(classDTO.getStudentName());
            Optional<StudentClassDTO> studentClasses = classService.findStudentClasses(classDTO.getStudentName());
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

        ClassListDTO allClasses = classService.findTeachersClasses(studentName);
        List<Integer> mondayClassList = allClasses.getMondayClassList();
        List<Integer> tuesdayClassList = allClasses.getTuesdayClassList();
        List<Integer> wednesdayClassList = allClasses.getWednesdayClassList();
        List<Integer> thursdayClassList = allClasses.getThursdayClassList();
        List<Integer> fridayClassList = allClasses.getFridayClassList();

        model.addAttribute("classDTO", new ClassDTO());
        model.addAttribute("studentClassList", new StudentClassDTO());

        model.addAttribute("studentName", studentName);
        model.addAttribute("classInMondayList", mondayClassList);
        model.addAttribute("classInTuesdayList", tuesdayClassList);
        model.addAttribute("classInWednesdayList", wednesdayClassList);
        model.addAttribute("classInThursdayList", thursdayClassList);
        model.addAttribute("classInFridayList", fridayClassList);
    }

    /*
     * 수업 수정시 할 경우, 학생 이름으로 조회한 결과.
     *
     * 수업 리스트 정보를 가져온 후, 조회 학생의 시간표를 제외하여 수업 시간표를 출력한 후
     * 그 결과에 조회 학생의 정보를 재출력
     * */

    private void searchStudentClass(StudentClassDTO studentClassesList, Model model) {

        ClassListDTO allClasses = classService.findTeachersClasses(studentClassesList.getStudentName());

        List<Integer> mondayClassList = allClasses.getMondayClassList();
        List<Integer> tuesdayClassList = allClasses.getTuesdayClassList();
        List<Integer> wednesdayClassList = allClasses.getWednesdayClassList();
        List<Integer> thursdayClassList = allClasses.getThursdayClassList();
        List<Integer> fridayClassList = allClasses.getFridayClassList();

        mondayClassList.remove(studentClassesList.getMonday());
        tuesdayClassList.remove(studentClassesList.getTuesday());
        wednesdayClassList.remove(studentClassesList.getWednesday());
        thursdayClassList.remove(studentClassesList.getThursday());
        fridayClassList.remove(studentClassesList.getFriday());

        model.addAttribute("classDTO", new ClassDTO());

        model.addAttribute("studentName", studentClassesList.getStudentName());

        model.addAttribute("classInMondayList", mondayClassList);
        model.addAttribute("classInTuesdayList", tuesdayClassList);
        model.addAttribute("classInWednesdayList", wednesdayClassList);
        model.addAttribute("classInThursdayList", thursdayClassList);
        model.addAttribute("classInFridayList", fridayClassList);

        model.addAttribute("studentClassList", studentClassesList);
    }
}
