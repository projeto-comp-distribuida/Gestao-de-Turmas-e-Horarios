package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade AcademicCalendar - Representa o calendário acadêmico.
 */
@Entity
@Table(name = "academic_calendars", indexes = {
    @Index(name = "idx_academic_calendar_school", columnList = "school_id"),
    @Index(name = "idx_academic_calendar_year", columnList = "academic_year")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"school"})
@ToString(callSuper = true, exclude = {"school"})
public class AcademicCalendar extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "academic_year", nullable = false, length = 10)
    private String academicYear;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
