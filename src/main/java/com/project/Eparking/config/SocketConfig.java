package com.project.Eparking.config;

import com.project.Eparking.service.impl.PrivateWebSocketHandler;
import com.project.Eparking.service.impl.socketPLO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SocketConfig implements WebSocketConfigurer {
    private final DelayTimeSocket delayTimeSocket;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(new PrivateWebSocketHandler(), "/privateReservation");
            registry.addHandler(new socketPLO(), "/privatePLO");
    }
}
