package com.distrischool.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicação principal do microserviço de Gestão de Turmas e Horários.
 * 
 * @EnableFeignClients - Habilita comunicação com outros microserviços via Feign
 * @EnableKafka - Habilita integração com Apache Kafka
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class ScheduleManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleManagementApplication.class, args);
    }
}
