package com.distrischool.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respostas de cursos (subjects)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDTO {
    
    private Long id;
    private String name;
    private String code;
    private Integer workloadHours;
    private String description;
    private Long academicCenterId;
    private String academicCenterName;
    private String academicCenterCode;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




