package com.distrischool.schedule.controller;

import com.distrischool.schedule.dto.ApiResponse;
import com.distrischool.schedule.dto.AttendanceRequestDTO;
import com.distrischool.schedule.dto.AttendanceResponseDTO;
import com.distrischool.schedule.entity.Attendance;
import com.distrischool.schedule.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar presenças (attendance).
 */
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AttendanceController.class);
    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> markAttendance(
            @RequestBody AttendanceRequestDTO dto) {
        String markedBy = getCurrentUsername();
        List<Attendance> attendances = attendanceService.markAttendance(dto, markedBy);
        List<AttendanceResponseDTO> responses = attendances.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Presença registrada com sucesso"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> updateAttendance(
            @PathVariable Long id,
            @RequestParam Boolean present) {
        String updatedBy = getCurrentUsername();
        Attendance attendance = attendanceService.updateAttendance(id, present, updatedBy);
        AttendanceResponseDTO response = mapToResponseDTO(attendance);
        return ResponseEntity.ok(ApiResponse.success(response, "Presença atualizada com sucesso"));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendanceBySchedule(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Attendance> attendances = attendanceService.getAttendanceBySchedule(scheduleId, date);
        List<AttendanceResponseDTO> responses = attendances.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Presenças encontradas"));
    }

    @GetMapping("/student/{studentId}/schedule/{scheduleId}")
    public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getStudentAttendance(
            @PathVariable Long studentId,
            @PathVariable Long scheduleId) {
        List<Attendance> attendances = attendanceService.getStudentAttendance(studentId, scheduleId);
        List<AttendanceResponseDTO> responses = attendances.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Presenças do estudante encontradas"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AttendanceResponseDTO>> findById(@PathVariable Long id) {
        Attendance attendance = attendanceService.findById(id);
        AttendanceResponseDTO response = mapToResponseDTO(attendance);
        return ResponseEntity.ok(ApiResponse.success(response, "Presença encontrada"));
    }

    private AttendanceResponseDTO mapToResponseDTO(Attendance attendance) {
        AttendanceResponseDTO.AttendanceResponseDTOBuilder builder = AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .scheduleId(attendance.getSchedule().getId())
                .studentId(attendance.getStudentId())
                .date(attendance.getDate())
                .present(attendance.getPresent())
                .markedBy(attendance.getMarkedBy())
                .notes(attendance.getNotes())
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt());

        if (attendance.getSchedule() != null && attendance.getSchedule().getClassEntity() != null) {
            builder.classId(attendance.getSchedule().getClassEntity().getId())
                   .className(attendance.getSchedule().getClassEntity().getName());
        }

        if (attendance.getSchedule() != null && attendance.getSchedule().getSubject() != null) {
            builder.subjectId(attendance.getSchedule().getSubject().getId())
                   .subjectName(attendance.getSchedule().getSubject().getName());
        }

        return builder.build();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }
}



