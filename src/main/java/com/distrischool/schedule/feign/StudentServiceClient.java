package com.distrischool.schedule.feign;

import com.distrischool.schedule.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * Feign client para comunicação com o serviço de gestão de alunos (gestao-de-alunos)
 */
@FeignClient(
    name = "student-service",
    url = "${microservice.student.url:http://gestao-de-alunos-dev:8080}"
)
public interface StudentServiceClient {

    /**
     * Busca um aluno por ID
     */
    @GetMapping("/api/v1/students/{studentId}")
    ApiResponse<Map<String, Object>> getStudentById(@PathVariable Long studentId);

    /**
     * Busca múltiplos alunos por IDs (validação em lote)
     */
    @PostMapping("/api/v1/students/batch")
    ApiResponse<List<Map<String, Object>>> getStudentsByIds(@RequestBody List<Long> studentIds);
}



