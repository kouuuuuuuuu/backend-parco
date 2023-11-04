package com.project.Eparking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.domain.SocketMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.TimeZone;
@SpringBootApplication
public class EparkingApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}
	public static void main(String[] args) {
		SpringApplication.run(EparkingApplication.class, args);
	}
}
