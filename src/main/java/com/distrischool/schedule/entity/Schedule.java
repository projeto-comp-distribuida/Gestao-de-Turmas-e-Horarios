package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Entidade Schedule - Representa um horário de aula.
 */
@Entity
@Table(name = "schedules", indexes = {
    @Index(name = "idx_schedule_class", columnList = "class_id"),
    @Index(name = "idx_schedule_subject", columnList = "subject_id"),
    @Index(name = "idx_schedule_shift", columnList = "shift_id"),
    @Index(name = "idx_schedule_day_time", columnList = "day_of_week, start_time, end_time"),
    @Index(name = "idx_schedule_room", columnList = "room"),
    @Index(name = "idx_schedule_teacher", columnList = "teacher_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"classEntity", "subject", "shift"})
@ToString(callSuper = true, exclude = {"classEntity", "subject", "shift"})
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(length = 100)
    private String room;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /**
     * Verifica se este horário conflita com outro horário
     */
    public boolean conflictsWith(Schedule other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        
        if (!this.room.equals(other.room) && 
            (this.teacherId == null || !this.teacherId.equals(other.teacherId))) {
            return false;
        }
        
        return (this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime));
    }
}
