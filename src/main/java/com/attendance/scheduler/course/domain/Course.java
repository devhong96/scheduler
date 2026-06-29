package com.attendance.scheduler.course.domain;

import com.attendance.scheduler.common.domain.BaseEntity;
import com.attendance.scheduler.student.domain.Student;
import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = PROTECTED)
public class Course extends BaseEntity {

    @Id
    private Long id;

    // 요일별 교시: 해당 요일 수업이 없으면 null (의도된 nullable)
    private Integer monday;
    private Integer tuesday;
    private Integer wednesday;
    private Integer thursday;
    private Integer friday;

    @Version
    private Long version;

    @NotNull
    @ManyToOne(fetch = LAZY)
    private Teacher teacherEntity;

    public void setTeacherEntity(Teacher teacherEntity) {
        if (this.teacherEntity != null) {
            this.teacherEntity.setClassEntity(null);
        }
        this.teacherEntity = teacherEntity;
        if (teacherEntity != null) {
            teacherEntity.setClassEntity(this);
        }
    }

    @MapsId
    @OneToOne(fetch = LAZY)
    private Student studentEntity;

    public void setStudentEntity(Student studentEntity) {
        if(this.studentEntity != null){
            this.studentEntity.addClassEntity(null);
        }
        this.studentEntity = studentEntity;
        if (studentEntity != null) {
            studentEntity.addClassEntity(this);
        }
    }

    @Builder
    public Course(Integer monday, Integer tuesday, Integer wednesday, Integer thursday, Integer friday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }

    public void updateSchedule(Integer monday, Integer tuesday, Integer wednesday, Integer thursday, Integer friday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
    }
}