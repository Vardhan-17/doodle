package com.doodle.controllers;

import com.doodle.models.Message;
import com.doodle.services.DoodleService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class Controller {
    private final DoodleService doodleService;

    @MessageMapping("/user/messages")
    public void send(@Payload Message message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        doodleService.handleUserMessage(message, simpMessageHeaderAccessor.getSessionId());
    }

    @MessageMapping("/room/messages")
    public void sendToRoom(Message message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        doodleService.handleRoomMessage(message, simpMessageHeaderAccessor.getSessionId());
    }
}
