package com.distrischool.template.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Registra métricas genéricas para o microserviço de turmas/horários.
 */
@Component
public class TemplateMetricsRecorder {

    private static final String METRIC_TEMPLATE_EVENTS = "template_events_total";

    private final MeterRegistry meterRegistry;

    public TemplateMetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordKafkaEvent(String direction, String eventType, String outcome) {
        meterRegistry.counter(
            METRIC_TEMPLATE_EVENTS,
            "direction", direction,
            "event_type", eventType,
            "outcome", outcome
        ).increment();
    }
}


