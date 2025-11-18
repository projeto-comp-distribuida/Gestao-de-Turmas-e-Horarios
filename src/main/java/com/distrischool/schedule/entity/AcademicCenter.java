package com.distrischool.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade AcademicCenter - Representa um centro acadêmico.
 * Centros acadêmicos são pré-definidos e contêm múltiplos cursos/disciplinas.
 */
@Entity
@Table(name = "academic_centers", indexes = {
    @Index(name = "idx_academic_center_code", columnList = "code"),
    @Index(name = "idx_academic_center_active", columnList = "active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"subjects"})
@ToString(callSuper = true, exclude = {"subjects"})
public class AcademicCenter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "academicCenter", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private Set<Subject> subjects = new HashSet<>();
}



