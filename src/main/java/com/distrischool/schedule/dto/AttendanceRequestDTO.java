package com.distrischool.schedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * DTO para requisições de registro de presença
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequestDTO {
    
    @NotNull(message = "ID do schedule é obrigatório")
    private Long scheduleId;
    
    @NotNull(message = "Data é obrigatória")
    private LocalDate date;
    
    /**
     * Mapa de studentId -> presente (true/false)
     */
    @Builder.Default
    private Map<Long, Boolean> studentPresence = new HashMap<>();
    
    private String notes;
}




