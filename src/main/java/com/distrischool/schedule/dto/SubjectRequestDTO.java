package com.distrischool.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para requisições de criação/atualização de cursos (subjects)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectRequestDTO {
    
    @NotBlank(message = "Nome do curso é obrigatório")
    private String name;
    
    private String code;
    
    private Integer workloadHours;
    
    private String description;
    
    private Long academicCenterId;
}




