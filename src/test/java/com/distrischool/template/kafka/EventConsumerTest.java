package com.distrischool.template.kafka;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for EventConsumer.
 * Tests Kafka event consumption functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventConsumer - Unit Tests")
public class EventConsumerTest {

    @InjectMocks
    private EventConsumer eventConsumer;

    private DistriSchoolEvent testEvent;

    @BeforeEach
    void setUp() {
        Map<String, Object> eventData = Map.of(
            "studentId", 1L,
            "action", "created"
        );
        
        testEvent = DistriSchoolEvent.create(
            "student.created",
            "student-service",
            eventData
        );
    }

    @Test
    @DisplayName("Deve processar evento recebido do Kafka")
    void shouldProcessReceivedEvent() {
        // Act & Assert - não deve lançar exceção
        assertThatCode(() -> {
            eventConsumer.consumeEvent(
                testEvent,
                "distrischool.events",
                0,
                1L
            );
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve processar evento schedule.updated")
    void shouldProcessScheduleUpdatedEvent() {
        // Arrange
        Map<String, Object> scheduleData = Map.of(
            "scheduleId", 123L,
            "classId", 456L
        );
        
        DistriSchoolEvent scheduleEvent = DistriSchoolEvent.create(
            "schedule.updated",
            "schedule-service",
            scheduleData
        );

        // Act & Assert
        assertThatCode(() -> {
            eventConsumer.consumeEvent(
                scheduleEvent,
                "distrischool.schedule.updated",
                0,
                1L
            );
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve processar evento student.created")
    void shouldProcessStudentCreatedEvent() {
        // Arrange
        Map<String, Object> studentData = Map.of(
            "studentId", 789L,
            "fullName", "João Silva"
        );
        
        DistriSchoolEvent studentEvent = DistriSchoolEvent.create(
            "student.created",
            "student-service",
            studentData
        );

        // Act & Assert
        assertThatCode(() -> {
            eventConsumer.consumeEvent(
                studentEvent,
                "distrischool.student.created",
                0,
                2L
            );
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Deve tratar erro ao processar evento inválido")
    void shouldHandleInvalidEvent() {
        // Arrange - evento com dados nulos
        DistriSchoolEvent invalidEvent = DistriSchoolEvent.create(
            "invalid.event",
            "unknown-service",
            null
        );

        // Act & Assert - deve tratar erro sem quebrar
        assertThatCode(() -> {
            eventConsumer.consumeEvent(
                invalidEvent,
                "distrischool.events",
                0,
                3L
            );
        }).doesNotThrowAnyException();
    }
}
