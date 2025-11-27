package com.distrischool.schedule.service;

import com.distrischool.schedule.dto.ScheduleResponseDTO;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.exception.ResourceNotFoundException;
import com.distrischool.schedule.kafka.ScheduleEventProducer;
import com.distrischool.schedule.repository.ClassRepository;
import com.distrischool.schedule.repository.ScheduleRepository;
import com.distrischool.schedule.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar horários (schedules).
 */
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ScheduleConflictService conflictService;
    private final ScheduleEventProducer eventProducer;

    @Transactional
    public Schedule create(Schedule schedule) {
        log.info("Criando schedule para turma: {}", schedule.getClassEntity() != null ? schedule.getClassEntity().getId() : "null");
        
        // Validar que a turma existe
        if (schedule.getClassEntity() == null || schedule.getClassEntity().getId() == null) {
            throw new IllegalArgumentException("Turma (class) é obrigatória para criar um schedule");
        }
        classRepository.findById(schedule.getClassEntity().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma", schedule.getClassEntity().getId()));
        
        // Validar que a disciplina existe
        if (schedule.getSubject() == null || schedule.getSubject().getId() == null) {
            throw new IllegalArgumentException("Disciplina (subject) é obrigatória para criar um schedule");
        }
        subjectRepository.findById(schedule.getSubject().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Disciplina", schedule.getSubject().getId()));
        
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
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));

        // Validar que a turma existe (se fornecida)
        if (schedule.getClassEntity() != null && schedule.getClassEntity().getId() != null) {
            classRepository.findById(schedule.getClassEntity().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Turma", schedule.getClassEntity().getId()));
            existing.setClassEntity(schedule.getClassEntity());
        }
        
        // Validar que a disciplina existe (se fornecida)
        if (schedule.getSubject() != null && schedule.getSubject().getId() != null) {
            subjectRepository.findById(schedule.getSubject().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Disciplina", schedule.getSubject().getId()));
            existing.setSubject(schedule.getSubject());
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));
    }

    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    /**
     * Busca um schedule por ID com relacionamentos carregados e retorna como DTO
     */
    public ScheduleResponseDTO findByIdAsDTO(Long id) {
        Schedule schedule = scheduleRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));
        return mapToDTO(schedule);
    }

    /**
     * Busca todos os schedules com relacionamentos carregados e retorna como DTOs
     */
    public List<ScheduleResponseDTO> findAllAsDTO() {
        List<Schedule> schedules = scheduleRepository.findAllWithRelations();
        return schedules.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Mapeia uma entidade Schedule para ScheduleResponseDTO
     */
    public ScheduleResponseDTO mapToDTO(Schedule schedule) {
        ScheduleResponseDTO.ScheduleResponseDTOBuilder builder = ScheduleResponseDTO.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek() != null ? schedule.getDayOfWeek().toString() : null)
                .startTime(schedule.getStartTime() != null ? schedule.getStartTime().toString() : null)
                .endTime(schedule.getEndTime() != null ? schedule.getEndTime().toString() : null)
                .room(schedule.getRoom())
                .teacherId(schedule.getTeacherId())
                .active(schedule.getActive())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt());

        // Mapear Class
        if (schedule.getClassEntity() != null) {
            builder.classId(schedule.getClassEntity().getId())
                   .className(schedule.getClassEntity().getName())
                   .classCode(schedule.getClassEntity().getCode());
        }

        // Mapear Subject
        if (schedule.getSubject() != null) {
            builder.subjectId(schedule.getSubject().getId())
                   .subjectName(schedule.getSubject().getName())
                   .subjectCode(schedule.getSubject().getCode());
        }

        // Mapear Shift
        if (schedule.getShift() != null) {
            builder.shiftId(schedule.getShift().getId())
                   .shiftName(schedule.getShift().getName());
        }

        return builder.build();
    }
}
