package com.distrischool.schedule.service;

import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.repository.ClassRepository;
import com.distrischool.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para detectar conflitos em horários e turmas.
 * Verifica conflitos tanto em schedules quanto em classes (conflitos de sala).
 */
@Service
@RequiredArgsConstructor
public class ScheduleConflictService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleConflictService.class);
    private final ScheduleRepository scheduleRepository;
    private final ClassRepository classRepository;

    @Transactional(readOnly = true)
    public List<Schedule> detectConflicts(Schedule schedule) {
        List<Schedule> conflicts = new ArrayList<>();

        if (schedule.getRoom() != null) {
            List<Schedule> roomConflicts = scheduleRepository.findRoomConflicts(
                    schedule.getRoom(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getId() != null ? schedule.getId() : -1L
            );
            conflicts.addAll(roomConflicts);
        }

        if (schedule.getTeacherId() != null) {
            List<Schedule> teacherConflicts = scheduleRepository.findTeacherConflicts(
                    schedule.getTeacherId(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getId() != null ? schedule.getId() : -1L
            );
            conflicts.addAll(teacherConflicts);
        }

        if (schedule.getClassEntity() != null && schedule.getClassEntity().getId() != null) {
            List<Schedule> classConflicts = scheduleRepository.findClassConflicts(
                    schedule.getClassEntity().getId(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    schedule.getId() != null ? schedule.getId() : -1L
            );
            conflicts.addAll(classConflicts);
        }

        return conflicts;
    }

    /**
     * Detecta conflitos de sala em nível de classe.
     * Verifica se a sala da classe está sendo usada por outra classe no mesmo horário.
     */
    @Transactional(readOnly = true)
    public List<Class> detectClassRoomConflicts(Schedule schedule) {
        List<Class> conflicts = new ArrayList<>();

        if (schedule.getClassEntity() != null && schedule.getClassEntity().getRoom() != null) {
            String room = schedule.getClassEntity().getRoom();
            Long excludeClassId = schedule.getClassEntity().getId();

            List<Class> roomConflicts = classRepository.findRoomConflicts(
                    room,
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    excludeClassId
            );
            conflicts.addAll(roomConflicts);
        }

        return conflicts;
    }

    /**
     * Detecta todos os tipos de conflitos: schedule e class room conflicts
     */
    @Transactional(readOnly = true)
    public ConflictReport detectAllConflicts(Schedule schedule) {
        List<Schedule> scheduleConflicts = detectConflicts(schedule);
        List<Class> classRoomConflicts = detectClassRoomConflicts(schedule);

        return ConflictReport.builder()
                .scheduleConflicts(scheduleConflicts)
                .classRoomConflicts(classRoomConflicts)
                .hasConflicts(!scheduleConflicts.isEmpty() || !classRoomConflicts.isEmpty())
                .build();
    }

    public boolean hasConflicts(Schedule schedule) {
        return !detectConflicts(schedule).isEmpty() || !detectClassRoomConflicts(schedule).isEmpty();
    }

    /**
     * Relatório de conflitos
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ConflictReport {
        private List<Schedule> scheduleConflicts;
        private List<Class> classRoomConflicts;
        private boolean hasConflicts;
    }
}
