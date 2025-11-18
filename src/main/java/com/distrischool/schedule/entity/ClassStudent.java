package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade ClassStudent - Tabela de junção para relacionamento Many-to-Many
 * entre Class e Student (referência externa via student_id BIGINT).
 */
@Entity
@Table(name = "class_students", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"class_id", "student_id"}),
       indexes = {
           @Index(name = "idx_class_student_class", columnList = "class_id"),
           @Index(name = "idx_class_student_student", columnList = "student_id")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"classEntity"})
@ToString(exclude = {"classEntity"})
public class ClassStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private Class classEntity;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 255)
    private String createdBy;
}



