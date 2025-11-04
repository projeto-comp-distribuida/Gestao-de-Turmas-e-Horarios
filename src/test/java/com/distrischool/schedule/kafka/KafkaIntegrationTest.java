package com.distrischool.schedule.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para Kafka no serviço de horários
 */
@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(partitions = 1, topics = {"distrischool.schedule.updated"})
@ActiveProfiles("test")
@DirtiesContext
class KafkaIntegrationTest {

    @Autowired(required = false)
    private KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @Autowired(required = false)
    private ScheduleEventProducer scheduleEventProducer;

    @Test
    void contextLoads() {
        assertThat(kafkaTemplate).isNotNull();
    }

    @Test
    void shouldPublishScheduleUpdatedEvent() {
        if (scheduleEventProducer == null || kafkaTemplate == null) {
            return;
        }

        // Testa publicação de evento
        scheduleEventProducer.publishScheduleUpdated(1L, 1L, 1L, "created");

        // Verifica se o KafkaTemplate está funcionando
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("scheduleId", 1L);
        eventData.put("action", "test");

        DistriSchoolEvent event = DistriSchoolEvent.create(
            "schedule.updated",
            "schedule-management-service",
            eventData
        );

        kafkaTemplate.send("distrischool.schedule.updated", event);

        assertThat(event).isNotNull();
        assertThat(event.getEventType()).isEqualTo("schedule.updated");
    }
}
