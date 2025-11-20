package com.distrischool.schedule.controller;

import com.distrischool.schedule.dto.ApiResponse;
import com.distrischool.schedule.dto.SubjectRequestDTO;
import com.distrischool.schedule.dto.SubjectResponseDTO;
import com.distrischool.schedule.entity.Subject;
import com.distrischool.schedule.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar cursos (subjects).
 */
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SubjectController.class);
    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> create(@RequestBody SubjectRequestDTO dto) {
        Subject created = subjectService.create(dto);
        SubjectResponseDTO response = mapToResponseDTO(created);
        return ResponseEntity.ok(ApiResponse.success(response, "Curso criado com sucesso"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody SubjectRequestDTO dto) {
        Subject updated = subjectService.update(id, dto);
        SubjectResponseDTO response = mapToResponseDTO(updated);
        return ResponseEntity.ok(ApiResponse.success(response, "Curso atualizado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubjectResponseDTO>> findById(@PathVariable Long id) {
        Subject subject = subjectService.findById(id);
        SubjectResponseDTO response = mapToResponseDTO(subject);
        return ResponseEntity.ok(ApiResponse.success(response, "Curso encontrado"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> findAll() {
        List<Subject> subjects = subjectService.findAll();
        List<SubjectResponseDTO> responses = subjects.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Cursos listados com sucesso"));
    }

    @GetMapping("/academic-center/{academicCenterId}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDTO>>> findByAcademicCenter(
            @PathVariable Long academicCenterId) {
        List<Subject> subjects = subjectService.findByAcademicCenter(academicCenterId);
        List<SubjectResponseDTO> responses = subjects.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses, "Cursos do centro acadêmico listados com sucesso"));
    }

    private SubjectResponseDTO mapToResponseDTO(Subject subject) {
        SubjectResponseDTO.SubjectResponseDTOBuilder builder = SubjectResponseDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .workloadHours(subject.getWorkloadHours())
                .description(subject.getDescription())
                .active(subject.getActive())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt());

        if (subject.getAcademicCenter() != null) {
            builder.academicCenterId(subject.getAcademicCenter().getId())
                   .academicCenterName(subject.getAcademicCenter().getName())
                   .academicCenterCode(subject.getAcademicCenter().getCode());
        }

        return builder.build();
    }
}

