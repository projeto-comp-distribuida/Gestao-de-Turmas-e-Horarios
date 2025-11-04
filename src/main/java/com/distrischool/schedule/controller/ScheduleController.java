package com.distrischool.schedule.controller;

import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.service.ScheduleService;
import com.distrischool.schedule.service.ScheduleConflictService;
import com.distrischool.schedule.websocket.ScheduleWebSocketController;
import com.distrischool.schedule.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<ApiResponse<Schedule>> create(@RequestBody Schedule schedule) {
        Schedule created = scheduleService.create(schedule);
        
        // Notificar via WebSocket
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("action", "created");
        updateMap.put("scheduleId", created.getId());
        websocketController.broadcastScheduleUpdate(updateMap);
        
        return ResponseEntity.ok(ApiResponse.success(created, "Horário criado com sucesso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Schedule>> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        Schedule updated = scheduleService.update(id, schedule);
        
        // Notificar via WebSocket
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("action", "updated");
        updateMap.put("scheduleId", updated.getId());
        websocketController.broadcastScheduleUpdate(updateMap);
        
        return ResponseEntity.ok(ApiResponse.success(updated, "Horário atualizado com sucesso"));
    }

    @DeleteMapping("/{id}")
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
    public ResponseEntity<ApiResponse<Schedule>> findById(@PathVariable Long id) {
        Schedule schedule = scheduleService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(schedule, "Horário encontrado"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Schedule>>> findAll() {
        List<Schedule> schedules = scheduleService.findAll();
        return ResponseEntity.ok(ApiResponse.success(schedules, "Horários listados com sucesso"));
    }

    @PostMapping("/{id}/check-conflicts")
    public ResponseEntity<ApiResponse<List<Schedule>>> checkConflicts(@PathVariable Long id) {
        Schedule schedule = scheduleService.findById(id);
        List<Schedule> conflicts = conflictService.detectConflicts(schedule);
        return ResponseEntity.ok(ApiResponse.success(conflicts, "Conflitos detectados"));
    }
}
