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
 * Feign client para comunicação com o serviço de gestão de professores (gestao-de-professores)
 */
@FeignClient(
    name = "teacher-service",
    url = "${microservice.teacher.url:http://teacher-management-service-dev:8080}"
)
public interface TeacherServiceClient {

    /**
     * Busca um professor por ID
     */
    @GetMapping("/api/v1/teachers/{teacherId}")
    ApiResponse<Map<String, Object>> getTeacherById(@PathVariable Long teacherId);

    /**
     * Busca múltiplos professores por IDs (validação em lote)
     */
    @PostMapping("/api/v1/teachers/batch")
    ApiResponse<List<Map<String, Object>>> getTeachersByIds(@RequestBody List<Long> teacherIds);
}




