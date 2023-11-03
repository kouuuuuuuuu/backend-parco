package com.project.Eparking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
@Configuration
@EnableWebSocket
public class DelayTimeSocket extends SockJsServiceRegistration {
    @Override
    public SockJsServiceRegistration setDisconnectDelay(long disconnectDelay) {
        return super.setDisconnectDelay(disconnectDelay);
    }

    @Override
    public SockJsServiceRegistration setHeartbeatTime(long heartbeatTime) {
        return super.setHeartbeatTime(heartbeatTime);
    }
}
