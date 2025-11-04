package com.distrischool.schedule.service;

import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.kafka.ScheduleEventProducer;
import com.distrischool.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço para gerenciar horários (schedules).
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final ScheduleConflictService conflictService;
    private final ScheduleEventProducer eventProducer;

    @Transactional
    public Schedule create(Schedule schedule) {
        // Verificar conflitos
        List<Schedule> conflicts = conflictService.detectConflicts(schedule);
        if (!conflicts.isEmpty()) {
            log.warn("Conflitos detectados ao criar schedule: {}", conflicts.size());
        }

        Schedule saved = scheduleRepository.save(schedule);
        
        // Publicar evento Kafka
        eventProducer.publishScheduleUpdated(
                saved.getId(),
                saved.getClassEntity() != null ? saved.getClassEntity().getId() : null,
                saved.getSubject() != null ? saved.getSubject().getId() : null,
                "created"
        );

        log.info("Schedule criado: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Schedule update(Long id, Schedule schedule) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule não encontrado: " + id));

        existing.setDayOfWeek(schedule.getDayOfWeek());
        existing.setStartTime(schedule.getStartTime());
        existing.setEndTime(schedule.getEndTime());
        existing.setRoom(schedule.getRoom());
        existing.setTeacherId(schedule.getTeacherId());
        existing.setActive(schedule.getActive());

        List<Schedule> conflicts = conflictService.detectConflicts(existing);
        if (!conflicts.isEmpty()) {
            log.warn("Conflitos detectados ao atualizar schedule: {}", conflicts.size());
        }

        Schedule saved = scheduleRepository.save(existing);
        
        eventProducer.publishScheduleUpdated(
                saved.getId(),
                saved.getClassEntity() != null ? saved.getClassEntity().getId() : null,
                saved.getSubject() != null ? saved.getSubject().getId() : null,
                "updated"
        );

        log.info("Schedule atualizado: {}", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule não encontrado: " + id));

        scheduleRepository.delete(schedule);
        
        eventProducer.publishScheduleUpdated(
                schedule.getId(),
                schedule.getClassEntity() != null ? schedule.getClassEntity().getId() : null,
                schedule.getSubject() != null ? schedule.getSubject().getId() : null,
                "deleted"
        );

        log.info("Schedule deletado: {}", id);
    }

    public Schedule findById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule não encontrado: " + id));
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }
}
