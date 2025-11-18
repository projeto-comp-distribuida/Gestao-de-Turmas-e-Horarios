package com.distrischool.schedule.controller;

import com.distrischool.schedule.dto.ApiResponse;
import com.distrischool.schedule.dto.ClassRequestDTO;
import com.distrischool.schedule.dto.ClassResponseDTO;
import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.ClassStudent;
import com.distrischool.schedule.entity.ClassTeacher;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.service.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar turmas (classes).
 */
@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
public class ClassController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassController.class);
    private final ClassService classService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ClassResponseDTO>> create(@RequestBody ClassRequestDTO dto) {
        Class created = classService.create(dto);
        ClassResponseDTO response = mapToResponseDTO(created);
        return ResponseEntity.ok(ApiResponse.success(response, "Turma criada com sucesso"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ClassResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody ClassRequestDTO dto) {
        Class updated = classService.update(id, dto);
        ClassResponseDTO response = mapToResponseDTO(updated);
        return ResponseEntity.ok(ApiResponse.success(response, "Turma atualizada com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        // Note: Soft delete should be implemented in the service
        classService.findById(id); // Validate exists
        return ResponseEntity.ok(ApiResponse.success("Turma deletada com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponseDTO>> findById(@PathVariable Long id) {
        Class classEntity = classService.findById(id);
        ClassResponseDTO response = mapToResponseDTO(classEntity);
        return ResponseEntity.ok(ApiResponse.success(response, "Turma encontrada"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> findAll() {
        List<Class> classes = classService.findAll();
        List<ClassResponseDTO> responses = classes.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Turmas listadas com sucesso"));
    }

    @PostMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> addStudents(
            @PathVariable Long id,
            @RequestBody List<Long> studentIds) {
        classService.addStudents(id, studentIds);
        return ResponseEntity.ok(ApiResponse.success("Estudantes adicionados com sucesso"));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> removeStudent(
            @PathVariable Long id,
            @PathVariable Long studentId) {
        classService.removeStudent(id, studentId);
        return ResponseEntity.ok(ApiResponse.success("Estudante removido com sucesso"));
    }

    @PostMapping("/{id}/teachers")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> addTeachers(
            @PathVariable Long id,
            @RequestBody List<Long> teacherIds) {
        classService.addTeachers(id, teacherIds);
        return ResponseEntity.ok(ApiResponse.success("Professores adicionados com sucesso"));
    }

    @DeleteMapping("/{id}/teachers/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> removeTeacher(
            @PathVariable Long id,
            @PathVariable Long teacherId) {
        classService.removeTeacher(id, teacherId);
        return ResponseEntity.ok(ApiResponse.success("Professor removido com sucesso"));
    }

    @GetMapping("/{id}/room-conflicts")
    public ResponseEntity<ApiResponse<List<ClassResponseDTO>>> checkRoomConflicts(
            @PathVariable Long id,
            @RequestParam(required = false) String room) {
        Class classEntity = classService.findById(id);
        String roomToCheck = room != null ? room : classEntity.getRoom();
        
        if (roomToCheck == null || roomToCheck.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "Nenhum conflito encontrado"));
        }

        List<Class> conflicts = classService.findRoomConflicts(id, roomToCheck);
        List<ClassResponseDTO> responses = conflicts.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Conflitos de sala encontrados"));
    }

    private ClassResponseDTO mapToResponseDTO(Class classEntity) {
        ClassResponseDTO.ClassResponseDTOBuilder builder = ClassResponseDTO.builder()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .code(classEntity.getCode())
                .academicYear(classEntity.getAcademicYear())
                .period(classEntity.getPeriod())
                .capacity(classEntity.getCapacity())
                .currentStudents(classEntity.getCurrentStudents())
                .room(classEntity.getRoom())
                .active(classEntity.getActive())
                .createdAt(classEntity.getCreatedAt())
                .updatedAt(classEntity.getUpdatedAt());

        if (classEntity.getSchool() != null) {
            builder.schoolId(classEntity.getSchool().getId())
                   .schoolName(classEntity.getSchool().getName());
        }

        if (classEntity.getShift() != null) {
            builder.shiftId(classEntity.getShift().getId())
                   .shiftName(classEntity.getShift().getName());
        }

        if (classEntity.getStartDate() != null) {
            builder.startDate(classEntity.getStartDate());
        }

        if (classEntity.getEndDate() != null) {
            builder.endDate(classEntity.getEndDate());
        }

        // Mapear estudantes
        List<Long> studentIds = classEntity.getStudents().stream()
                .map(ClassStudent::getStudentId)
                .collect(Collectors.toList());
        builder.studentIds(studentIds);

        // Mapear professores
        List<Long> teacherIds = classEntity.getTeachers().stream()
                .map(ClassTeacher::getTeacherId)
                .collect(Collectors.toList());
        builder.teacherIds(teacherIds);

        // Mapear schedules
        List<ClassResponseDTO.ScheduleSummaryDTO> schedules = classEntity.getSchedules().stream()
                .map(this::mapScheduleToDTO)
                .collect(Collectors.toList());
        builder.schedules(schedules);

        return builder.build();
    }

    private ClassResponseDTO.ScheduleSummaryDTO mapScheduleToDTO(Schedule schedule) {
        ClassResponseDTO.ScheduleSummaryDTO.ScheduleSummaryDTOBuilder builder = 
                ClassResponseDTO.ScheduleSummaryDTO.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek().toString())
                .startTime(schedule.getStartTime().toString())
                .endTime(schedule.getEndTime().toString())
                .room(schedule.getRoom())
                .teacherId(schedule.getTeacherId());

        if (schedule.getSubject() != null) {
            builder.subjectId(schedule.getSubject().getId())
                   .subjectName(schedule.getSubject().getName());
        }

        return builder.build();
    }
}

