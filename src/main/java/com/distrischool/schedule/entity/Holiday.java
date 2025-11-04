package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade Holiday - Representa feriados e dias sem aula.
 */
@Entity
@Table(name = "holidays", indexes = {
    @Index(name = "idx_holiday_date", columnList = "date"),
    @Index(name = "idx_holiday_school", columnList = "school_id"),
    @Index(name = "idx_holiday_type", columnList = "type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"school"})
@ToString(callSuper = true, exclude = {"school"})
public class Holiday extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private HolidayType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean recurring = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    private School school;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public enum HolidayType {
        NATIONAL,    // Nacional
        STATE,       // Estadual
        MUNICIPAL,   // Municipal
        SCHOOL       // Escolar
    }
}
