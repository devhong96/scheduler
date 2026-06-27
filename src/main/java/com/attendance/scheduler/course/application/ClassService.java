package com.attendance.scheduler.course.application;

import com.attendance.scheduler.course.domain.ClassEntity;
import com.attendance.scheduler.course.dto.ClassDTO;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.domain.StudentEntity;
import com.attendance.scheduler.student.dto.ClassListDTO;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassJpaRepository classJpaRepository;
    private final StudentJpaRepository studentJpaRepository;
    private final ClassRepository classRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public List<ClassDTO> findStudentClassList() {
        return classRepository.getStudentClassList();
    }

    @Transactional
    public ClassListDTO findTeachersClasses(String studentName) {

        //학생 이름으로
        StudentEntity studentEntity
                = studentJpaRepository.findStudentEntityByStudentName(studentName);

        List<StudentClassDTO> studentClassByTeacherName
                = classRepository.getStudentClassByTeacherEntity(studentEntity.getTeacherEntity());

        ClassListDTO classListDTO = ClassListDTO.getInstance();

        classListDTO.setStudentName(studentName);
        for (StudentClassDTO classDTO : studentClassByTeacherName) {
            classListDTO.getMondayClassList().add(classDTO.getMonday());
            classListDTO.getTuesdayClassList().add(classDTO.getTuesday());
            classListDTO.getWednesdayClassList().add(classDTO.getWednesday());
            classListDTO.getThursdayClassList().add(classDTO.getThursday());
            classListDTO.getFridayClassList().add(classDTO.getFriday());
        }
        return classListDTO;
    }

    public Optional<StudentClassDTO> findStudentClasses(String studentName) {
        return Optional.ofNullable(classRepository.getStudentClassByStudentName(studentName));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveClassTable(ClassDTO classDTO)  {

        classValidator(classDTO);

        StudentEntity studentEntity = studentJpaRepository.findStudentEntityByStudentName(classDTO.getStudentName());

        ClassEntity classEntity = classDTO.toEntity();
        classEntity.setTeacherEntity(studentEntity.getTeacherEntity());
        classEntity.setStudentEntity(studentEntity);
        classJpaRepository.save(classEntity);

//        eventPublisher.publishEvent(new CourseEvent(classEntity.getTeacherEntity(), classDTO.getStudentName()+ "의 수업 등록(혹은 변경)이 있습니다."));
    }

    private void classValidator(ClassDTO classDTO) {
        Optional<StudentEntity> studentEntityByStudentName = Optional.ofNullable(studentJpaRepository.findStudentEntityByStudentName(classDTO.getStudentName()));

        if (studentEntityByStudentName.isEmpty()) {
            duplicateClassValidator(classDTO);
        } else {
            classJpaRepository.deleteById(studentEntityByStudentName.get().getId());
            duplicateClassValidator(classDTO);
        }
    }

    private void duplicateClassValidator(ClassDTO classDTO) {

        List<ClassDTO> allClassDTO = classRepository.getStudentClassList();

        for (ClassDTO classDTOList: allClassDTO) {
            Integer mondayValue = classDTOList.getMonday();
            Integer tuesdayValue = classDTOList.getTuesday();
            Integer wednesdayValue = classDTOList.getWednesday();
            Integer thursdayValue = classDTOList.getThursday();
            Integer fridayValue = classDTOList.getFriday();

            if (mondayValue.equals(classDTO.getMonday())) throw new IllegalStateException("월요일 수업 중에 겹치는 날이 있습니다.");
            if (tuesdayValue.equals(classDTO.getTuesday())) throw new IllegalStateException("화요일 수업 중에 겹치는 날이 있습니다.");
            if (wednesdayValue.equals(classDTO.getWednesday())) throw new IllegalStateException("수요일 수업 중에 겹치는 날이 있습니다.");
            if (thursdayValue.equals(classDTO.getThursday())) throw new IllegalStateException("목요일 수업 중에 겹치는 날이 있습니다.");
            if (fridayValue.equals(classDTO.getFriday())) throw new IllegalStateException("금요일 수업 중에 겹치는 날이 있습니다.");
        }
    }

    @Transactional
    public void deleteClass(String studentName) {
        StudentEntity studentEntity = studentJpaRepository.findStudentEntityByStudentName(studentName);
        classJpaRepository.deleteClassEntityByStudentEntity(studentEntity);
    }
}