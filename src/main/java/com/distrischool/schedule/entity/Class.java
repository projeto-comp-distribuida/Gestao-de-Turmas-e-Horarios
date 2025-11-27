package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade Class - Representa uma turma/classe.
 */
@Entity
@Table(name = "classes", indexes = {
    @Index(name = "idx_class_code", columnList = "code"),
    @Index(name = "idx_class_school", columnList = "school_id"),
    @Index(name = "idx_class_shift", columnList = "shift_id"),
    @Index(name = "idx_class_academic_year", columnList = "academic_year"),
    @Index(name = "idx_class_room", columnList = "room")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"school", "shift", "subject", "schedules", "students", "teachers"})
@ToString(callSuper = true, exclude = {"school", "shift", "subject", "schedules", "students", "teachers"})
public class Class extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String code;

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    @Column(length = 50)
    private String period;

    @Column
    private Integer capacity;

    @Column(name = "current_students")
    @Builder.Default
    private Integer currentStudents = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(length = 100)
    private String room;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Schedule> schedules = new HashSet<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ClassStudent> students = new HashSet<>();

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ClassTeacher> teachers = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
