package com.example.broadcastserver.config;

import com.example.broadcastserver.server.BroadcastHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers (WebSocketHandlerRegistry registry) {
        registry.addHandler (broadcastHandler (), "/chat")
                .setAllowedOrigins ("*");
    }

    @Bean
    public BroadcastHandler broadcastHandler () {
        return new BroadcastHandler ();
    }
}
