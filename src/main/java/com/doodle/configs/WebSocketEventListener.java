package com.doodle.configs;

import com.doodle.services.DoodleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@AllArgsConstructor
public class WebSocketEventListener {
    private DoodleService doodleService;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        doodleService.handleUserDisconnect(sessionId);
    }
}
