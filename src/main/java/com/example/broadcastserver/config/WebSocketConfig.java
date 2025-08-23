package com.example.broadcastserver.config;

import com.example.broadcastserver.server.BroadcastHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private BroadcastHandler handler;

    @Override
    public void registerWebSocketHandlers (WebSocketHandlerRegistry registry) {
        registry.addHandler (this.handler, "/chat")
                .setAllowedOrigins ("*");
    }

    @Bean
    public BroadcastHandler handler () {
        return new BroadcastHandler ();
    }
}
