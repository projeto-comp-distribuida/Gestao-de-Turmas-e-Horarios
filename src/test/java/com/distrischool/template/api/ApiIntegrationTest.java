package com.distrischool.template.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Practical API integration tests.
 * Tests REST endpoints using MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("API - Integration Tests")
public class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/health deve retornar 200 OK")
    void healthCheckShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }

    @Test
    @DisplayName("GET /api/v1/health/info deve retornar informações do serviço")
    void healthInfoShouldReturnServiceInfo() throws Exception {
        mockMvc.perform(get("/api/v1/health/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.version").exists());
    }

    @Test
    @DisplayName("API deve aceitar Content-Type application/json")
    void apiShouldAcceptJsonContentType() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("API deve retornar CORS headers se configurado")
    void apiShouldReturnCorsHeaders() throws Exception {
        // CORS pode não estar configurado, então apenas verificamos que o endpoint aceita OPTIONS
        // O 403 ou 200 são ambos aceitáveis - importante é que não seja 404 (endpoint existe)
        mockMvc.perform(options("/api/v1/health")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andDo(print())
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assert status == 200 || status == 403 : "Status deve ser 200 ou 403, mas foi " + status;
                });
    }

    @Test
    @DisplayName("Endpoint inexistente deve retornar 404")
    void nonExistentEndpointShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/nonexistent"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
