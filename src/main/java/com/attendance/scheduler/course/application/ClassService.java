package com.attendance.scheduler.course.application;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.course.dto.ClassResponse;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.dto.ClassListResponse;
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
    public List<ClassResponse> findStudentClassList() {
        return classRepository.getStudentClassList();
    }

    @Transactional
    public ClassListResponse findTeachersClasses(String studentName) {

        Student studentEntity
                = studentJpaRepository.findStudentEntityByStudentName(studentName);

        List<StudentClassResponse> studentClassByTeacherName
                = classRepository.getStudentClassByTeacherEntity(studentEntity.getTeacherEntity());

        ClassListResponse classListDTO = ClassListResponse.getInstance();

        classListDTO = classListDTO.withStudentName(studentName);
        for (StudentClassResponse classDTO : studentClassByTeacherName) {
            classListDTO.mondayClassList().add(classDTO.monday());
            classListDTO.tuesdayClassList().add(classDTO.tuesday());
            classListDTO.wednesdayClassList().add(classDTO.wednesday());
            classListDTO.thursdayClassList().add(classDTO.thursday());
            classListDTO.fridayClassList().add(classDTO.friday());
        }
        return classListDTO;
    }

    public Optional<StudentClassResponse> findStudentClasses(String studentName) {
        return Optional.ofNullable(classRepository.getStudentClassByStudentName(studentName));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveClassTable(ClassRequest classRequest) {

        duplicateClassValidator(classRequest);

        Student studentEntity = studentJpaRepository.findStudentEntityByStudentName(classRequest.studentName());

        // Course는 @MapsId로 학생과 PK를 공유(학생당 수업 1개)하므로, 삭제 후 재삽입이 아니라
        // 기존 수업이 있으면 제자리에서 갱신(upsert)한다. 그래야 공유 PK 충돌이 발생하지 않는다.
        Course classEntity = classRepository
                .getStudentClassEntityByStudentName(classRequest.studentName())
                .orElseGet(() -> {
                    Course newCourse = classRequest.toEntity();
                    newCourse.setStudentEntity(studentEntity);
                    return newCourse;
                });

        classEntity.setTeacherEntity(studentEntity.getTeacherEntity());
        classEntity.updateSchedule(classRequest.monday(), classRequest.tuesday(),
                classRequest.wednesday(), classRequest.thursday(), classRequest.friday());
        classJpaRepository.save(classEntity);
    }

    private void duplicateClassValidator(ClassRequest classRequest) {

        List<ClassResponse> allClassDTO = classRepository.getStudentClassList();

        for (ClassResponse classDTOList : allClassDTO) {
            // 본인의 기존 수업은 충돌 대상에서 제외(수업 변경 시 자기 자신과 겹치는 것을 방지)
            if (classDTOList.studentName().equals(classRequest.studentName())) {
                continue;
            }
            Integer mondayValue = classDTOList.monday();
            Integer tuesdayValue = classDTOList.tuesday();
            Integer wednesdayValue = classDTOList.wednesday();
            Integer thursdayValue = classDTOList.thursday();
            Integer fridayValue = classDTOList.friday();

            if (mondayValue.equals(classRequest.monday())) throw new IllegalStateException("월요일 수업 중에 겹치는 날이 있습니다.");
            if (tuesdayValue.equals(classRequest.tuesday())) throw new IllegalStateException("화요일 수업 중에 겹치는 날이 있습니다.");
            if (wednesdayValue.equals(classRequest.wednesday())) throw new IllegalStateException("수요일 수업 중에 겹치는 날이 있습니다.");
            if (thursdayValue.equals(classRequest.thursday())) throw new IllegalStateException("목요일 수업 중에 겹치는 날이 있습니다.");
            if (fridayValue.equals(classRequest.friday())) throw new IllegalStateException("금요일 수업 중에 겹치는 날이 있습니다.");
        }
    }

    @Transactional
    public void deleteClass(String studentName) {
        Student studentEntity = studentJpaRepository.findStudentEntityByStudentName(studentName);
        classJpaRepository.deleteClassEntityByStudentEntity(studentEntity);
    }
}
