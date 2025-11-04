package com.distrischool.schedule.controller;

import com.distrischool.schedule.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller para health checks do microserviço
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        log.info("GET /api/v1/health - Verificando saúde do serviço");
        
        Map<String, Object> healthInfo = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "DistriSchool Schedule Management Service",
            "version", "1.0.0"
        );
        
        return ResponseEntity.ok(ApiResponse.success(healthInfo, "Serviço funcionando corretamente"));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInfo() {
        log.info("GET /api/v1/health/info - Obtendo informações do serviço");
        
        Map<String, Object> serviceInfo = Map.of(
            "name", "DistriSchool Schedule Management Service",
            "description", "Microserviço de Gestão de Turmas e Horários",
            "version", "1.0.0",
            "features", new String[]{
                "Spring Boot 3.2.0",
                "PostgreSQL com Flyway",
                "Redis para cache",
                "Apache Kafka para mensageria",
                "Spring WebSocket para atualizações em tempo real",
                "Importação Excel",
                "Detecção de conflitos de horários"
            }
        );
        
        return ResponseEntity.ok(ApiResponse.success(serviceInfo, "Informações do serviço"));
    }
}
