package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade Attendance - Representa o registro de presença de um aluno em uma sessão de aula.
 * A presença está vinculada a um Schedule (horário específico), não diretamente à Class.
 */
@Entity
@Table(name = "attendance",
       uniqueConstraints = @UniqueConstraint(columnNames = {"schedule_id", "student_id", "date"}),
       indexes = {
           @Index(name = "idx_attendance_schedule", columnList = "schedule_id"),
           @Index(name = "idx_attendance_student", columnList = "student_id"),
           @Index(name = "idx_attendance_date", columnList = "date"),
           @Index(name = "idx_attendance_schedule_date", columnList = "schedule_id, date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"schedule"})
@ToString(exclude = {"schedule"})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private Boolean present = true;

    @Column(name = "marked_by", length = 255)
    private String markedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "updated_by", length = 255)
    private String updatedBy;
}



