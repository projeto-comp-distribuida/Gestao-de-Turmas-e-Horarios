package com.distrischool.schedule.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Controller WebSocket para notificações de atualizações de horários.
 */
@Controller
public class ScheduleWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast de atualização de horário para todos os clientes.
     */
    public void broadcastScheduleUpdate(Map<String, Object> update) {
        messagingTemplate.convertAndSend("/topic/schedule/updates", update);
    }

    /**
     * Notifica atualização de horário para uma escola específica.
     */
    public void notifySchoolScheduleUpdate(Long schoolId, Map<String, Object> update) {
        messagingTemplate.convertAndSend("/topic/schedule/school/" + schoolId, update);
    }

    /**
     * Endpoint para receber mensagens dos clientes.
     */
    @MessageMapping("/schedule/subscribe")
    @SendTo("/topic/schedule/updates")
    public Map<String, Object> subscribe(Map<String, Object> message) {
        return Map.of("status", "subscribed", "message", message);
    }
}
