package com.attendance.scheduler.course.application;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.dto.ClassRequest;
import com.attendance.scheduler.course.dto.ClassResponse;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.course.event.CourseEvent;
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

        // 담당 교사에게 수업 신청 알림 발행 (CourseEventListener 가 Notification 생성)
        eventPublisher.publishEvent(new CourseEvent(
                studentEntity.getTeacherEntity(),
                classRequest.studentName() + " 학생이 수업을 신청했습니다."));
    }

    private void duplicateClassValidator(ClassRequest classRequest) {

        List<ClassResponse> allClassDTO = classRepository.getStudentClassList();

        for (ClassResponse classDTOList : allClassDTO) {
            // 본인의 기존 수업은 충돌 대상에서 제외(수업 변경 시 자기 자신과 겹치는 것을 방지)
            if (classDTOList.studentName().equals(classRequest.studentName())) {
                continue;
            }
            // 0(등원 안 함)은 실제 수업이 아니므로 겹침 검사에서 제외한다.
            if (collides(classDTOList.monday(), classRequest.monday())) throw new IllegalStateException("월요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.tuesday(), classRequest.tuesday())) throw new IllegalStateException("화요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.wednesday(), classRequest.wednesday())) throw new IllegalStateException("수요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.thursday(), classRequest.thursday())) throw new IllegalStateException("목요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.friday(), classRequest.friday())) throw new IllegalStateException("금요일 수업 중에 겹치는 날이 있습니다.");
        }
    }

    // 같은 교시를 신청했는지 판단. 0(등원 안 함)/null 은 수업이 아니므로 충돌로 보지 않는다.
    private boolean collides(Integer existing, Integer requested) {
        return existing != null && existing != 0 && existing.equals(requested);
    }

    @Transactional
    public void deleteClass(String studentName) {
        Student studentEntity = studentJpaRepository.findStudentEntityByStudentName(studentName);
        Course classEntity = studentEntity.getClassEntity();
        if (classEntity == null) {
            return;
        }
        // 양방향 연관(student.classEntity)을 먼저 끊어야 flush 시
        // TransientPropertyValueException(삭제된 Course 를 여전히 참조) 를 피할 수 있다.
        studentEntity.addClassEntity(null);
        classJpaRepository.delete(classEntity);
    }
}
