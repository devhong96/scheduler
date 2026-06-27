package com.attendance.scheduler.teacher.domain;

import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.notification.domain.Notification;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.teacher.dto.EditEmailDTO;
import com.attendance.scheduler.teacher.dto.PwdEditDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Teacher {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String username;

    private String teacherName;

    private String password;

    private String email;

    @Transient
    private String role;

    @Column(columnDefinition = "boolean default '0'")
    private boolean approved;

    @OneToMany(mappedBy = "teacherEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Student> studentEntityList = new ArrayList<>();

    public void setClassEntity(Course classEntity) {
        if(classEntityList == null) classEntityList = new ArrayList<>();
        this.classEntityList.add(classEntity);
    }

    @OneToMany(mappedBy = "teacherEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Course> classEntityList = new ArrayList<>();

    public void setStudentEntity(Student studentEntity) {
        if(studentEntityList == null) studentEntityList = new ArrayList<>();
        this.studentEntityList.add(studentEntity);
    }

    @OneToMany(mappedBy = "teacherEntity", cascade = CascadeType.ALL)
    List<Notification> notificationEntityList = new ArrayList<>();

    public void setNotificationEntity(Notification notificationEntity) {
        if(notificationEntityList == null) notificationEntityList = new ArrayList<>();
        this.notificationEntityList.add(notificationEntity);
    }

    public void updatePassword(PwdEditDTO pwdEditDTO) {
        this.password = pwdEditDTO.getPassword();
    }

    public void updateEmail(EditEmailDTO editEmailDTO) {
        this.email = editEmailDTO.getEmail();
    }

    public void updateApprove(boolean approved) {
        this.approved = approved;
    }

    @Builder
    public Teacher(String username, String teacherName, String password, String email, String role, boolean approved) {
        this.username = username;
        this.teacherName = teacherName;
        this.password = password;
        this.email = email;
        this.role = role;
        this.approved = approved;
    }
}