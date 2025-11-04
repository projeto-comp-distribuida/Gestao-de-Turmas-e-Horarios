package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

/**
 * Entidade Shift - Representa turnos (Manhã, Tarde, Noite, Integral).
 */
@Entity
@Table(name = "shifts", indexes = {
    @Index(name = "idx_shift_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Shift extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public enum ShiftType {
        MORNING,      // Manhã
        AFTERNOON,    // Tarde
        EVENING,      // Noite
        FULL_TIME     // Integral
    }
}
