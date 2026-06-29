package com.attendance.scheduler.notification.domain;

import com.attendance.scheduler.common.domain.BaseEntity;
import com.attendance.scheduler.teacher.domain.Teacher;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@Table(name = "notice")
@NoArgsConstructor(access = PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(columnDefinition = "boolean default '0'")
    private boolean checked;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacherEntity;

    public void setTeacherEntity(Teacher teacherEntity) {
        if (this.teacherEntity != null) {
            this.teacherEntity.getNotificationEntityList().remove(this);
        }
        this.teacherEntity = teacherEntity;
        if(teacherEntity != null) {
            teacherEntity.setNotificationEntity(this);
        }
    }

    public void checked() {
        this.checked = true;
    }

    @Builder
    public Notification(Teacher teacherEntity, String message, boolean checked) {
        this.teacherEntity = teacherEntity;
        this.message = message;
        this.checked = checked;
    }
}
