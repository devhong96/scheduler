package com.attendance.scheduler.student.domain;

import com.attendance.scheduler.comment.domain.entity.Comment;
import com.attendance.scheduler.common.domain.BaseEntity;
import com.attendance.scheduler.course.domain.Course;
import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = PROTECTED)
public class Student extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String studentName;

    @Column(nullable = false, length = 11)
    private String studentPhoneNumber;

    @Column(length = 255)
    private String studentAddress;

    @Column(length = 255)
    private String studentDetailedAddress;

    @Column(length = 11)
    private String studentParentPhoneNumber;

    @NotNull
    @ManyToOne(fetch = LAZY, optional = false, cascade = CascadeType.PERSIST)
    private Teacher teacherEntity;

    public void setTeacherEntity(Teacher teacherEntity) {
        if (this.teacherEntity != null) {
            this.teacherEntity.getStudentEntityList().remove(this);
        }
        this.teacherEntity = teacherEntity;
        if(teacherEntity != null) {
            teacherEntity.setStudentEntity(this);
        }
    }

    @OneToMany(mappedBy = "studentEntity")
    List<Comment> commentEntityList = new ArrayList<>();

    public void addCommentEntity(Comment commentEntity) {
        this.commentEntityList.add(commentEntity);
    }

    @OneToOne(mappedBy = "studentEntity")
    @PrimaryKeyJoinColumn
    private Course classEntity;

    public void addClassEntity(Course classEntity) {
        this.classEntity = classEntity;
    }

    @Builder
    public Student(String studentName, String studentPhoneNumber, String studentAddress, String studentDetailedAddress, String studentParentPhoneNumber) {
        this.studentName = studentName;
        this.studentPhoneNumber = studentPhoneNumber;
        this.studentAddress = studentAddress;
        this.studentDetailedAddress = studentDetailedAddress;
        this.studentParentPhoneNumber = studentParentPhoneNumber;
    }
}
