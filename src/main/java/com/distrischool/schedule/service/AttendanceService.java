package com.distrischool.schedule.service;

import com.distrischool.schedule.dto.AttendanceRequestDTO;
import com.distrischool.schedule.entity.Attendance;
import com.distrischool.schedule.entity.ClassStudent;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.exception.BusinessException;
import com.distrischool.schedule.exception.ResourceNotFoundException;
import com.distrischool.schedule.kafka.AttendanceEventProducer;
import com.distrischool.schedule.repository.AttendanceRepository;
import com.distrischool.schedule.repository.ClassStudentRepository;
import com.distrischool.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar presenças (attendance).
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AttendanceService.class);
    
    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClassStudentRepository classStudentRepository;
    private final AttendanceEventProducer eventProducer;

    @Transactional
    public List<Attendance> markAttendance(AttendanceRequestDTO dto, String markedBy) {
        log.info("Registrando presença para schedule {} na data {}", dto.getScheduleId(), dto.getDate());

        // Validar schedule
        Schedule schedule = scheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", dto.getScheduleId()));

        // Validar que os estudantes estão na turma
        Long classId = schedule.getClassEntity().getId();
        List<ClassStudent> classStudents = classStudentRepository.findByClassId(classId);
        Map<Long, ClassStudent> studentMap = classStudents.stream()
                .collect(Collectors.toMap(ClassStudent::getStudentId, cs -> cs));

        List<Attendance> attendanceRecords = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry : dto.getStudentPresence().entrySet()) {
            Long studentId = entry.getKey();
            Boolean present = entry.getValue();

            // Verificar se o estudante está na turma
            if (!studentMap.containsKey(studentId)) {
                throw new BusinessException(
                        String.format("Estudante %d não está matriculado na turma %d", studentId, classId));
            }

            // Buscar ou criar registro de presença
            Attendance attendance = attendanceRepository
                    .findByScheduleIdAndStudentIdAndDate(dto.getScheduleId(), studentId, dto.getDate())
                    .orElse(Attendance.builder()
                            .schedule(schedule)
                            .studentId(studentId)
                            .date(dto.getDate())
                            .present(present)
                            .markedBy(markedBy)
                            .notes(dto.getNotes())
                            .build());

            attendance.setPresent(present);
            attendance.setMarkedBy(markedBy);
            if (dto.getNotes() != null) {
                attendance.setNotes(dto.getNotes());
            }

            Attendance saved = attendanceRepository.save(attendance);
            attendanceRecords.add(saved);
            
            // Publicar evento Kafka
            eventProducer.publishAttendanceRecorded(
                    saved.getId(),
                    saved.getSchedule().getId(),
                    saved.getStudentId(),
                    saved.getSchedule().getClassEntity().getId(),
                    saved.getPresent(),
                    saved.getMarkedBy()
            );
        }

        log.info("Presença registrada para {} estudantes", attendanceRecords.size());
        return attendanceRecords;
    }

    @Transactional
    public Attendance updateAttendance(Long attendanceId, Boolean present, String updatedBy) {
        log.info("Atualizando presença: {}", attendanceId);

        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Presença", attendanceId));

        attendance.setPresent(present);
        attendance.setUpdatedBy(updatedBy);
        attendance.setMarkedBy(updatedBy);

        Attendance saved = attendanceRepository.save(attendance);
        
        // Publicar evento Kafka
        eventProducer.publishAttendanceRecorded(
                saved.getId(),
                saved.getSchedule().getId(),
                saved.getStudentId(),
                saved.getSchedule().getClassEntity().getId(),
                saved.getPresent(),
                saved.getMarkedBy()
        );

        return saved;
    }

    public List<Attendance> getAttendanceBySchedule(Long scheduleId, LocalDate date) {
        log.info("Buscando presença para schedule {} na data {}", scheduleId, date);

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", scheduleId));

        if (date != null) {
            return attendanceRepository.findByScheduleIdAndDate(scheduleId, date);
        } else {
            return attendanceRepository.findByScheduleId(scheduleId);
        }
    }

    public List<Attendance> getStudentAttendance(Long studentId, Long scheduleId) {
        log.info("Buscando presença do estudante {} no schedule {}", studentId, scheduleId);
        return attendanceRepository.findByStudentIdAndScheduleId(studentId, scheduleId);
    }

    public Attendance findById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presença", id));
    }
}

