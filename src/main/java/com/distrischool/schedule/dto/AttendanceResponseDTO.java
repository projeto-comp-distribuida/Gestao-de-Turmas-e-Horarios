package com.distrischool.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para respostas de presença
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDTO {
    
    private Long id;
    private Long scheduleId;
    private Long classId;
    private String className;
    private Long subjectId;
    private String subjectName;
    private Long studentId;
    private LocalDate date;
    private Boolean present;
    private String markedBy;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceByScheduleDTO {
        private Long scheduleId;
        private LocalDate date;
        private List<StudentAttendanceDTO> students = new ArrayList<>();
        
        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class StudentAttendanceDTO {
            private Long studentId;
            private Boolean present;
            private String markedBy;
            private String notes;
            private LocalDateTime markedAt;
        }
    }
}




