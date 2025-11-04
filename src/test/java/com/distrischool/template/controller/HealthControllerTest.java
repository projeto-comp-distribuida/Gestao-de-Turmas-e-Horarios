package com.distrischool.template.controller;

import com.distrischool.template.config.TestContainersConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for HealthController.
 * Tests the health check endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
@DisplayName("HealthController - Integration Tests")
public class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar status UP no health check")
    void shouldReturnUpStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").exists())
                .andExpect(jsonPath("$.data.timestamp").exists());
    }

    @Test
    @DisplayName("Deve retornar informações do serviço")
    void shouldReturnServiceInfo() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/health/info")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.version").exists())
                .andExpect(jsonPath("$.data.features").isArray());
    }

    @Test
    @DisplayName("Deve retornar JSON válido no health check")
    void shouldReturnValidJson() throws Exception {
        // When & Then
        String response = mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verifica que é um JSON válido
        assert objectMapper.readTree(response) != null;
    }

    @Test
    @DisplayName("Health check deve ser rápido (< 500ms)")
    void shouldBeFast() throws Exception {
        // When & Then
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        
        long duration = System.currentTimeMillis() - startTime;
        
        // Assert - deve responder em menos de 500ms
        assert duration < 500 : "Health check demorou " + duration + "ms";
    }
}
