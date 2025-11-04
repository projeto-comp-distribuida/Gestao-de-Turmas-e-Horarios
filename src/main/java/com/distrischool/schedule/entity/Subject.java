package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade Subject - Representa uma disciplina/matéria.
 */
@Entity
@Table(name = "subjects", indexes = {
    @Index(name = "idx_subject_code", columnList = "code"),
    @Index(name = "idx_subject_school", columnList = "school_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"school"})
@ToString(callSuper = true, exclude = {"school"})
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(name = "workload_hours")
    private Integer workloadHours;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
