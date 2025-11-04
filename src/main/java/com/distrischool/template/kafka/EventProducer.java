package com.distrischool.template.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Producer Kafka para publicar eventos do DistriSchool.
 * Use este padrão para enviar mensagens para outros microserviços.
 */
@Component
public class EventProducer {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EventProducer.class);
    private final KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Envia um evento para um tópico específico
     */
    public void sendEvent(String topic, DistriSchoolEvent event) {
        log.info("Enviando evento para tópico '{}': {}", topic, event.getEventType());
        
        CompletableFuture<SendResult<String, DistriSchoolEvent>> future = 
            kafkaTemplate.send(topic, event.getEventId(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento enviado com sucesso. Tópico: {}, Partition: {}, Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Erro ao enviar evento para o tópico '{}': {}", 
                        topic, ex.getMessage(), ex);
            }
        });
    }

    /**
     * Envia um evento para um tópico específico com callback
     */
    public void sendEvent(String topic, DistriSchoolEvent event, 
                         CompletableFuture<SendResult<String, DistriSchoolEvent>> callback) {
        log.info("Enviando evento para tópico '{}': {}", topic, event.getEventType());
        
        CompletableFuture<SendResult<String, DistriSchoolEvent>> future = 
            kafkaTemplate.send(topic, event.getEventId(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Evento enviado com sucesso. Tópico: {}, Partition: {}, Offset: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Erro ao enviar evento para o tópico '{}': {}", 
                        topic, ex.getMessage(), ex);
            }
            if (callback != null) {
                callback.complete(result);
            }
        });
    }
}