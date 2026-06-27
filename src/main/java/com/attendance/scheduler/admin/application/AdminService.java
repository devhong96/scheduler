package com.attendance.scheduler.admin.application;

import com.attendance.scheduler.admin.domain.Admin;
import com.attendance.scheduler.admin.dto.ChangeTeacherDTO;
import com.attendance.scheduler.admin.dto.EmailDTO;
import com.attendance.scheduler.admin.repository.AdminJpaRepository;
import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.course.dto.StudentClassDTO;
import com.attendance.scheduler.course.repository.ClassJpaRepository;
import com.attendance.scheduler.course.repository.ClassRepository;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.student.repository.StudentJpaRepository;
import com.attendance.scheduler.teacher.domain.Teacher;
import com.attendance.scheduler.teacher.dto.TeacherDTO;
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

    public List<TeacherDTO> getTeacherList() {
        return teacherRepository.getTeacherList();
    }

    public List<TeacherDTO> findTeacherInformation(String username) {
        return teacherRepository.getTeacherInfoByUsername(username);
    }


    public Optional<EmailDTO> findAdminEmailByID(EmailDTO emailDTO) {
        Optional<Admin> adminAccount = Optional.ofNullable(adminJpaRepository
                .findByUsernameIs(emailDTO.getUsername()));
        return adminAccount.map(adminEntity -> EmailDTO.builder()
                .username(adminEntity.getUsername())
                .email(adminEntity.getEmail())
                .build());
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
    public void changeExistTeacher(ChangeTeacherDTO changeTeacherDTO) throws IllegalStateException{
        Long teacherId = changeTeacherDTO.getTeacherId();
        Long studentId = changeTeacherDTO.getStudentId();

        Teacher teacherEntity = teacherJpaRepository.findTeacherEntityById(teacherId);
        Student studentEntity = studentJpaRepository.findStudentEntityById(studentId);

        //학생의 수업엔티티와 교사의 수업을 비교
        List<StudentClassDTO> studentClassByTeacherEntity = classRepository.getStudentClassByTeacherEntity(teacherEntity);
        StudentClassDTO studentClassByStudentName = classRepository.getStudentClassByStudentName(studentEntity.getStudentName());

        classValidator(studentClassByStudentName, studentClassByTeacherEntity);
        studentEntity.setTeacherEntity(teacherEntity);

        Optional<Course> optionalClassEntity = classRepository.getStudentClassEntityByStudentName(studentEntity.getStudentName());

        if(optionalClassEntity.isPresent()) {
            Course classEntity = optionalClassEntity.get();
            classEntity.setTeacherEntity(teacherEntity);
            classJpaRepository.save(classEntity);
        }

        studentJpaRepository.save(studentEntity);
    }

    private void classValidator(StudentClassDTO studentClassByStudentName, List<StudentClassDTO> studentClassByTeacherEntity) {

        for (StudentClassDTO classDTOList : studentClassByTeacherEntity) {
            Integer mondayValue = classDTOList.getMonday();
            Integer tuesdayValue = classDTOList.getTuesday();
            Integer wednesdayValue = classDTOList.getWednesday();
            Integer thursdayValue = classDTOList.getThursday();
            Integer fridayValue = classDTOList.getFriday();
            if (mondayValue.equals(studentClassByStudentName.getMonday()))
                throw new IllegalStateException("학생의 월요일 수업 중에 겹치는 날이 있습니다.");
            if (tuesdayValue.equals(studentClassByStudentName.getTuesday()))
                throw new IllegalStateException("학생의 화요일 수업 중에 겹치는 날이 있습니다.");
            if (wednesdayValue.equals(studentClassByStudentName.getWednesday()))
                throw new IllegalStateException("학생의 수요일 수업 중에 겹치는 날이 있습니다.");
            if (thursdayValue.equals(studentClassByStudentName.getThursday()))
                throw new IllegalStateException("학생의 목요일 수업 중에 겹치는 날이 있습니다.");
            if (fridayValue.equals(studentClassByStudentName.getFriday()))
                throw new IllegalStateException("학생의 요일 수업 중에 겹치는 날이 있습니다.");
        }
    }


    @Transactional
    public void deleteTeacherAccount(String teacherId) {
        Optional<Teacher> teacherEntity = Optional.ofNullable(
                teacherJpaRepository.findByUsernameIs(teacherId));

        //교사 엔티티가 존재
        if(teacherEntity.isPresent()) {
            //수업이 있는 교사 엔티티를 찾는다.
            List<StudentClassDTO> studentClassByTeacherName
                    = classRepository.getStudentClassByTeacherEntity(teacherEntity.get());

            if(!studentClassByTeacherName.isEmpty())
                throw new IllegalStateException("학생 수업 시간이 남아 있습니다.");

            //교사 엔티티를 가져온다.
            classJpaRepository.deleteByTeacherEntity(teacherEntity.get());
            teacherJpaRepository.deleteByUsernameIs(teacherId);
        }
    }
}