package com.distrischool.schedule.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer para eventos relacionados a presenças (attendance).
 */
@Component
@RequiredArgsConstructor
public class AttendanceEventProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AttendanceEventProducer.class);
    private final KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @Value("${microservice.kafka.topics.attendance-recorded:distrischool.attendance.recorded}")
    private String attendanceRecordedTopic;

    public void publishAttendanceRecorded(Long attendanceId, Long scheduleId, Long studentId, 
                                          Long classId, Boolean present, String markedBy) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("attendanceId", attendanceId);
        eventData.put("scheduleId", scheduleId);
        eventData.put("studentId", studentId);
        eventData.put("classId", classId);
        eventData.put("present", present);
        eventData.put("markedBy", markedBy);
        eventData.put("timestamp", System.currentTimeMillis());

        DistriSchoolEvent event = DistriSchoolEvent.create(
                "attendance.recorded",
                "schedule-service",
                eventData
        );

        kafkaTemplate.send(attendanceRecordedTopic, event);
        log.info("Evento attendance.recorded publicado no tópico {}: attendanceId={}, studentId={}, present={}", 
                attendanceRecordedTopic, attendanceId, studentId, present);
    }
}

