package com.distrischool.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para requisições de criação/atualização de turmas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRequestDTO {
    
    @NotBlank(message = "Nome da turma é obrigatório")
    private String name;
    
    private String code;
    
    private String academicYear;
    
    private String period;
    
    private Integer capacity;
    
    private Long shiftId;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String room;
    
    @Builder.Default
    private List<Long> studentIds = new ArrayList<>();
    
    @Builder.Default
    private List<Long> teacherIds = new ArrayList<>();
}

