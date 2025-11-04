package com.distrischool.template.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * Evento base para comunicação entre microsserviços do DistriSchool.
 * Todos os eventos do sistema devem estender esta classe base.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistriSchoolEvent {

    private String eventId;
    private String eventType;
    private String source;
    private String version;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private Map<String, Object> metadata;

    // Getters e Setters manuais para garantir compatibilidade
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Cria um evento básico do DistriSchool
     */
    public static DistriSchoolEvent create(String eventType, String source, Map<String, Object> data) {
        DistriSchoolEvent event = new DistriSchoolEvent();
        event.setEventId(java.util.UUID.randomUUID().toString());
        event.setEventType(eventType);
        event.setSource(source);
        event.setVersion("1.0");
        event.setTimestamp(LocalDateTime.now());
        event.setData(data != null ? new HashMap<>(data) : new HashMap<>());
        return event;
    }

    /**
     * Adiciona metadados ao evento
     */
    public DistriSchoolEvent withMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }
}
