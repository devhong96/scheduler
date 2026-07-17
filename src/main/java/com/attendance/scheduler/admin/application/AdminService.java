package com.attendance.scheduler.admin.application;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.dto.ChangeTeacherRequest;
import com.attendance.scheduler.admin.dto.EmailResponse;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.dto.StudentClassResponse;
import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.TeacherResponse;
import com.attendance.scheduler.teacher.repository.TeacherJpaRepository;
import com.attendance.scheduler.teacher.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ClassJpaRepository classJpaRepository;
    private final AdminJpaRepository adminJpaRepository;
    private final TeacherJpaRepository teacherJpaRepository;
    private final TeacherRepository teacherRepository;
    private final StudentJpaRepository studentJpaRepository;
    private final ClassRepository classRepository;

    public List<TeacherResponse> getTeacherList() {
        return teacherRepository.getTeacherList();
    }

    public List<TeacherResponse> findTeacherInformation(String username) {
        return teacherRepository.getTeacherInfoByUsername(username);
    }

    public Optional<EmailResponse> findAdminEmailByID(EmailResponse emailDTO) {
        Optional<Admin> adminAccount = Optional.ofNullable(adminJpaRepository
                .findByUsernameIs(emailDTO.username()));
        return adminAccount.map(adminEntity -> new EmailResponse(
                adminEntity.getUsername(),
                adminEntity.getEmail()
        ));
    }

    @Transactional
    public void grantAuth(String teacherId) {
        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(teacherId);
        teacherEntity.updateApprove(true);
        teacherJpaRepository.save(teacherEntity);
    }

    @Transactional
    public void revokeAuth(String teacherId) {
        Teacher teacherEntity = teacherJpaRepository.findByUsernameIs(teacherId);
        teacherEntity.updateApprove(false);
        teacherJpaRepository.save(teacherEntity);
    }

    @Transactional
    public void changeExistTeacher(ChangeTeacherRequest changeTeacherDTO) throws IllegalStateException {
        Long teacherId = changeTeacherDTO.teacherId();
        Long studentId = changeTeacherDTO.studentId();

        Teacher teacherEntity = teacherJpaRepository.findTeacherEntityById(teacherId);
        Student studentEntity = studentJpaRepository.findStudentEntityById(studentId);

        List<StudentClassResponse> studentClassByTeacherEntity = classRepository.getStudentClassByTeacherEntity(teacherEntity);
        StudentClassResponse studentClassByStudentName = classRepository.getStudentClassByStudentName(studentEntity.getStudentName());

        classValidator(studentClassByStudentName, studentClassByTeacherEntity);
        studentEntity.setTeacherEntity(teacherEntity);

        Optional<Course> optionalClassEntity = classRepository.getStudentClassEntityByStudentName(studentEntity.getStudentName());

        if (optionalClassEntity.isPresent()) {
            Course classEntity = optionalClassEntity.get();
            classEntity.setTeacherEntity(teacherEntity);
            classJpaRepository.save(classEntity);
        }

        studentJpaRepository.save(studentEntity);
    }

    private void classValidator(StudentClassResponse studentClassByStudentName, List<StudentClassResponse> studentClassByTeacherEntity) {

        for (StudentClassResponse classDTOList : studentClassByTeacherEntity) {
            // 0(등원 안 함)은 실제 수업이 아니므로 겹침 검사에서 제외한다.
            if (collides(classDTOList.monday(), studentClassByStudentName.monday()))
                throw new IllegalStateException("학생의 월요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.tuesday(), studentClassByStudentName.tuesday()))
                throw new IllegalStateException("학생의 화요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.wednesday(), studentClassByStudentName.wednesday()))
                throw new IllegalStateException("학생의 수요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.thursday(), studentClassByStudentName.thursday()))
                throw new IllegalStateException("학생의 목요일 수업 중에 겹치는 날이 있습니다.");
            if (collides(classDTOList.friday(), studentClassByStudentName.friday()))
                throw new IllegalStateException("학생의 요일 수업 중에 겹치는 날이 있습니다.");
        }
    }

    // 같은 교시를 신청했는지 판단. 0(등원 안 함)/null 은 수업이 아니므로 충돌로 보지 않는다.
    private boolean collides(Integer existing, Integer target) {
        return existing != null && existing != 0 && existing.equals(target);
    }

    @Transactional
    public void deleteTeacherAccount(String teacherId) {
        Optional<Teacher> teacherEntity = Optional.ofNullable(
                teacherJpaRepository.findByUsernameIs(teacherId));

        if (teacherEntity.isPresent()) {
            List<StudentClassResponse> studentClassByTeacherName
                    = classRepository.getStudentClassByTeacherEntity(teacherEntity.get());

            if (!studentClassByTeacherName.isEmpty())
                throw new IllegalStateException("학생 수업 시간이 남아 있습니다.");

            classJpaRepository.deleteByTeacherEntity(teacherEntity.get());
            teacherJpaRepository.deleteByUsernameIs(teacherId);
        }
    }
}
