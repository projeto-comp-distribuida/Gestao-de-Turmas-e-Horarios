package com.distrischool.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respostas de horários (schedules)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponseDTO {
    
    private Long id;
    private Long classId;
    private String className;
    private String classCode;
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private Long shiftId;
    private String shiftName;
    private String dayOfWeek;
    private String startTime;
    private String endTime;
    private String room;
    private Long teacherId;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}




