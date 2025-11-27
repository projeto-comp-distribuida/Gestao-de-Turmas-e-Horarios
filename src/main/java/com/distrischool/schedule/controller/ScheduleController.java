package com.distrischool.schedule.controller;

import com.distrischool.schedule.dto.ScheduleResponseDTO;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.service.ScheduleService;
import com.distrischool.schedule.service.ScheduleConflictService;
import com.distrischool.schedule.websocket.ScheduleWebSocketController;
import com.distrischool.schedule.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciar horários (schedules).
 */
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleController.class);
    private final ScheduleService scheduleService;
    private final ScheduleConflictService conflictService;
    private final ScheduleWebSocketController websocketController;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> create(@RequestBody Schedule schedule) {
        Schedule created = scheduleService.create(schedule);
        
        // Notificar via WebSocket
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("action", "created");
        updateMap.put("scheduleId", created.getId());
        websocketController.broadcastScheduleUpdate(updateMap);
        
        // Retornar como DTO para evitar LazyInitializationException
        ScheduleResponseDTO dto = scheduleService.findByIdAsDTO(created.getId());
        return ResponseEntity.ok(ApiResponse.success(dto, "Horário criado com sucesso"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        Schedule updated = scheduleService.update(id, schedule);
        
        // Notificar via WebSocket
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("action", "updated");
        updateMap.put("scheduleId", updated.getId());
        websocketController.broadcastScheduleUpdate(updateMap);
        
        // Retornar como DTO para evitar LazyInitializationException
        ScheduleResponseDTO dto = scheduleService.findByIdAsDTO(updated.getId());
        return ResponseEntity.ok(ApiResponse.success(dto, "Horário atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        scheduleService.delete(id);
        
        // Notificar via WebSocket
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("action", "deleted");
        updateMap.put("scheduleId", id);
        websocketController.broadcastScheduleUpdate(updateMap);
        
        return ResponseEntity.ok(ApiResponse.success("Horário deletado com sucesso"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponseDTO>> findById(@PathVariable Long id) {
        ScheduleResponseDTO schedule = scheduleService.findByIdAsDTO(id);
        return ResponseEntity.ok(ApiResponse.success(schedule, "Horário encontrado"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> findAll() {
        List<ScheduleResponseDTO> schedules = scheduleService.findAllAsDTO();
        return ResponseEntity.ok(ApiResponse.success(schedules, "Horários listados com sucesso"));
    }

    @PostMapping("/{id}/check-conflicts")
    public ResponseEntity<ApiResponse<List<ScheduleResponseDTO>>> checkConflicts(@PathVariable Long id) {
        Schedule schedule = scheduleService.findById(id);
        List<Schedule> conflicts = conflictService.detectConflicts(schedule);
        List<ScheduleResponseDTO> conflictDTOs = conflicts.stream()
                .map(s -> scheduleService.mapToDTO(s))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(conflictDTOs, "Conflitos detectados"));
    }
}
