package com.distrischool.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para centros acadêmicos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicCenterDTO {
    
    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




