package com.distrischool.schedule.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer para eventos relacionados a turmas (classes).
 */
@Component
@RequiredArgsConstructor
public class ClassEventProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassEventProducer.class);
    private final KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @Value("${microservice.kafka.topics.class-created:distrischool.class.created}")
    private String classCreatedTopic;

    @Value("${microservice.kafka.topics.class-updated:distrischool.class.updated}")
    private String classUpdatedTopic;

    @Value("${microservice.kafka.topics.student-enrolled:distrischool.student.enrolled}")
    private String studentEnrolledTopic;

    public void publishClassCreated(Long classId, Long schoolId, String className) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("classId", classId);
        eventData.put("schoolId", schoolId);
        eventData.put("className", className);
        eventData.put("timestamp", System.currentTimeMillis());

        DistriSchoolEvent event = DistriSchoolEvent.create(
                "class.created",
                "schedule-service",
                eventData
        );

        kafkaTemplate.send(classCreatedTopic, event);
        log.info("Evento class.created publicado no tópico {}: classId={}, className={}", 
                classCreatedTopic, classId, className);
    }

    public void publishClassUpdated(Long classId, Long schoolId, String className) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("classId", classId);
        eventData.put("schoolId", schoolId);
        eventData.put("className", className);
        eventData.put("timestamp", System.currentTimeMillis());

        DistriSchoolEvent event = DistriSchoolEvent.create(
                "class.updated",
                "schedule-service",
                eventData
        );

        kafkaTemplate.send(classUpdatedTopic, event);
        log.info("Evento class.updated publicado no tópico {}: classId={}, className={}", 
                classUpdatedTopic, classId, className);
    }

    public void publishStudentEnrolled(Long classId, Long studentId, Long schoolId) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("classId", classId);
        eventData.put("studentId", studentId);
        eventData.put("schoolId", schoolId);
        eventData.put("timestamp", System.currentTimeMillis());

        DistriSchoolEvent event = DistriSchoolEvent.create(
                "student.enrolled",
                "schedule-service",
                eventData
        );

        kafkaTemplate.send(studentEnrolledTopic, event);
        log.info("Evento student.enrolled publicado no tópico {}: classId={}, studentId={}", 
                studentEnrolledTopic, classId, studentId);
    }
}

