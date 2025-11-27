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
 * DTO para respostas de turmas com relacionamentos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassResponseDTO {
    
    private Long id;
    private String name;
    private String code;
    private String academicYear;
    private String period;
    private Integer capacity;
    private Integer currentStudents;
    private Long schoolId;
    private String schoolName;
    private Long shiftId;
    private String shiftName;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String room;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Builder.Default
    private List<Long> studentIds = new ArrayList<>();
    
    @Builder.Default
    private List<Long> teacherIds = new ArrayList<>();
    
    @Builder.Default
    private List<ScheduleSummaryDTO> schedules = new ArrayList<>();
    
    @Builder.Default
    private List<SubjectSummaryDTO> subjects = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleSummaryDTO {
        private Long id;
        private Long subjectId;
        private String subjectName;
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String room;
        private Long teacherId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectSummaryDTO {
        private Long id;
        private String name;
        private String code;
    }
}




