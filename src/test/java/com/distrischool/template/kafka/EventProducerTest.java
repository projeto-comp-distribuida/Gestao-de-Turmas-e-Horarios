package com.distrischool.template.kafka;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventProducer.
 * Tests Kafka event publishing functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventProducer - Unit Tests")
public class EventProducerTest {

    @Mock
    private KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    private DistriSchoolEvent testEvent;
    private String testTopic;

    @BeforeEach
    void setUp() {
        testTopic = "distrischool.events";
        
        Map<String, Object> eventData = Map.of(
            "scheduleId", 1L,
            "action", "created",
            "timestamp", System.currentTimeMillis()
        );
        
        testEvent = DistriSchoolEvent.create(
            "schedule.updated",
            "schedule-service",
            eventData
        );
    }

    @Test
    @DisplayName("Deve publicar evento no Kafka com sucesso")
    void shouldPublishEventSuccessfully() {
        // Arrange
        SettableListenableFuture<SendResult<String, DistriSchoolEvent>> future = new SettableListenableFuture<>();
        SendResult<String, DistriSchoolEvent> sendResult = mock(SendResult.class);
        future.set(sendResult);
        
        when(kafkaTemplate.send(anyString(), anyString(), any(DistriSchoolEvent.class)))
                .thenReturn(new CompletableFuture<>());

        // Act
        eventProducer.sendEvent(testTopic, testEvent);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DistriSchoolEvent> eventCaptor = ArgumentCaptor.forClass(DistriSchoolEvent.class);

        verify(kafkaTemplate, times(1)).send(
            topicCaptor.capture(),
            keyCaptor.capture(),
            eventCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo(testTopic);
        assertThat(keyCaptor.getValue()).isEqualTo(testEvent.getEventId());
        assertThat(eventCaptor.getValue()).isEqualTo(testEvent);
        assertThat(eventCaptor.getValue().getEventType()).isEqualTo("schedule.updated");
        assertThat(eventCaptor.getValue().getSource()).isEqualTo("schedule-service");
    }

    @Test
    @DisplayName("Deve publicar evento com callback")
    void shouldPublishEventWithCallback() {
        // Arrange
        CompletableFuture<SendResult<String, DistriSchoolEvent>> callback = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any(DistriSchoolEvent.class)))
                .thenReturn(callback);

        // Act
        eventProducer.sendEvent(testTopic, testEvent, callback);

        // Assert
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(DistriSchoolEvent.class));
    }

    @Test
    @DisplayName("Deve tratar erro ao publicar evento")
    void shouldHandlePublishError() {
        // Arrange
        CompletableFuture<SendResult<String, DistriSchoolEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));
        
        when(kafkaTemplate.send(anyString(), anyString(), any(DistriSchoolEvent.class)))
                .thenReturn(future);

        // Act
        eventProducer.sendEvent(testTopic, testEvent);

        // Assert - verifica que o método foi chamado mesmo com erro
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any(DistriSchoolEvent.class));
    }

    @Test
    @DisplayName("Deve publicar evento schedule.updated")
    void shouldPublishScheduleUpdatedEvent() {
        // Arrange
        Map<String, Object> scheduleData = Map.of(
            "scheduleId", 123L,
            "classId", 456L,
            "dayOfWeek", "MONDAY",
            "startTime", "08:00",
            "endTime", "09:30"
        );
        
        DistriSchoolEvent scheduleEvent = DistriSchoolEvent.create(
            "schedule.updated",
            "schedule-service",
            scheduleData
        );

        when(kafkaTemplate.send(anyString(), anyString(), any(DistriSchoolEvent.class)))
                .thenReturn(new CompletableFuture<>());

        // Act
        eventProducer.sendEvent("distrischool.schedule.updated", scheduleEvent);

        // Assert
        ArgumentCaptor<DistriSchoolEvent> eventCaptor = ArgumentCaptor.forClass(DistriSchoolEvent.class);
        verify(kafkaTemplate).send(anyString(), anyString(), eventCaptor.capture());
        
        DistriSchoolEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getEventType()).isEqualTo("schedule.updated");
        assertThat(capturedEvent.getData()).containsKey("scheduleId");
        assertThat(capturedEvent.getData().get("scheduleId")).isEqualTo(123L);
    }
}
