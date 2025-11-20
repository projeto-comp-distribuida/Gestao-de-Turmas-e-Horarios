package com.distrischool.schedule.feign;

import com.distrischool.schedule.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client para comunicação com o serviço de autenticação
 */
@FeignClient(
    name = "auth-service",
    url = "${microservice.auth.url:http://auth-service-dev:8080}"
)
public interface AuthServiceClient {

    /**
     * Busca um usuário por ID
     */
    @GetMapping("/api/v1/users/{userId}")
    ApiResponse<Map<String, Object>> getUserById(@PathVariable Long userId);

    /**
     * Verifica se um usuário tem uma role específica
     * Retorna true se o usuário tem a role especificada
     */
    @GetMapping("/api/v1/users/{userId}/has-role")
    ApiResponse<Boolean> hasRole(@PathVariable Long userId, @RequestParam String role);

    /**
     * Busca um usuário por Auth0 ID
     */
    @GetMapping("/api/v1/users/auth0/{auth0Id}")
    ApiResponse<Map<String, Object>> getUserByAuth0Id(@PathVariable String auth0Id);
}

