package com.distrischool.schedule.service;

import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço para detectar conflitos em horários.
 */
@Service
@RequiredArgsConstructor
public class ScheduleConflictService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleConflictService.class);
    private final ScheduleRepository scheduleRepository;

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

    public boolean hasConflicts(Schedule schedule) {
        return !detectConflicts(schedule).isEmpty();
    }
}
