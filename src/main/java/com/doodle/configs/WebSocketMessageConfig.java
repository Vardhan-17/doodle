package com.doodle.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/app");
        config.setApplicationDestinationPrefixes("/server");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/connect")
//                .setAllowedOrigins("https://willingly-desired-colt.ngrok-free.app", "http://127.0.0.1:5500", "http://106.214.207.227:4200")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
