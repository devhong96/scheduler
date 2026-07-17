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
        // 이전 교사의 컬렉션에서 제거하지 않는다.
        // Teacher.studentEntityList 에 orphanRemoval=true 가 걸려 있어, 컬렉션에서 제거하면
        // 담당교사 변경(재배정) 시 학생이 고아로 간주되어 삭제되고 낙관적 락 충돌이 발생한다.
        // 소유측 FK(Student.teacherEntity)만 갱신하면 재배정이 올바르게 반영된다.
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
