package com.distrischool.schedule.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Configuração do Feign Client para adicionar automaticamente o token JWT
 * no header Authorization de todas as requisições Feign.
 */
@Configuration
@Slf4j
public class FeignConfig {

    @Value("${security.disable:false}")
    private boolean securityDisable;

    /**
     * Interceptor que adiciona o token JWT do SecurityContext ao header Authorization
     * de todas as requisições Feign.
     * 
     * Se a segurança estiver desabilitada ou não houver autenticação, o interceptor
     * não adiciona o header Authorization.
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Se a segurança estiver desabilitada, não adiciona o token
                if (securityDisable) {
                    log.debug("Segurança desabilitada - token JWT não será adicionado à requisição Feign");
                    return;
                }

                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    
                    if (authentication != null && authentication instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                        Jwt jwt = jwtAuth.getToken();
                        
                        if (jwt != null) {
                            String tokenValue = jwt.getTokenValue();
                            template.header("Authorization", "Bearer " + tokenValue);
                            log.debug("Token JWT adicionado ao header Authorization da requisição Feign para: {}", 
                                    template.url());
                        } else {
                            log.warn("JWT token é null no SecurityContext - requisição Feign pode falhar com 401");
                        }
                    } else {
                        log.warn("Nenhuma autenticação JWT encontrada no SecurityContext para requisição Feign: {} - " +
                                "requisição pode falhar com 401", template.url());
                    }
                } catch (Exception e) {
                    log.error("Erro ao adicionar token JWT à requisição Feign: {}", e.getMessage(), e);
                }
            }
        };
    }
}

