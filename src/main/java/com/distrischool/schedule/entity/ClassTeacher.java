package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade ClassTeacher - Tabela de junção para relacionamento Many-to-Many
 * entre Class e Teacher (referência externa via teacher_id BIGINT).
 */
@Entity
@Table(name = "class_teachers",
       uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "teacher_id"}),
       indexes = {
           @Index(name = "idx_class_teacher_class", columnList = "class_id"),
           @Index(name = "idx_class_teacher_teacher", columnList = "teacher_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"classEntity"})
@ToString(exclude = {"classEntity"})
public class ClassTeacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;

    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;
}




