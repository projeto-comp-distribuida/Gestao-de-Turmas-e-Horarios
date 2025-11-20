package com.distrischool.schedule.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer para eventos de schedule (horários).
 */
@Component
@RequiredArgsConstructor
public class ScheduleEventProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduleEventProducer.class);
    private final KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @Value("${microservice.kafka.topics.schedule-updated:distrischool.schedule.updated}")
    private String scheduleUpdatedTopic;

    public void publishScheduleUpdated(Long scheduleId, Long classId, Long subjectId, String action) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("scheduleId", scheduleId);
        eventData.put("classId", classId);
        eventData.put("subjectId", subjectId);
        eventData.put("action", action);
        eventData.put("timestamp", System.currentTimeMillis());

        DistriSchoolEvent event = DistriSchoolEvent.create(
                "schedule.updated",
                "schedule-service",
                eventData
        );

        kafkaTemplate.send(scheduleUpdatedTopic, event);
        log.info("Evento schedule.updated publicado no tópico {}: scheduleId={}, action={}", 
                scheduleUpdatedTopic, scheduleId, action);
    }
}
