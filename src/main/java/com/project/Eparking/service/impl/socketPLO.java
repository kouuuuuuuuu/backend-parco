package com.project.Eparking.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.domain.SocketMessage;
import com.project.Eparking.domain.SocketPLO;
import com.project.Eparking.exception.ApiRequestException;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class socketPLO implements WebSocketHandler {
    private List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private Map<String, List<WebSocketSession>> chatRooms = new HashMap<>();



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("The connection is success");
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        var clientValue = message.getPayload();
        System.out.println("The send message is success: " + clientValue);
        SocketPLO socketMessage = convertToChatMessage((String) clientValue);

        assert socketMessage != null;
        List<WebSocketSession> participants = chatRooms.computeIfAbsent(socketMessage.getPloID(), k -> new ArrayList<>());
        if (!participants.contains(session)) {
            participants.add(session);
        }
        if(participants!=null){
            for (WebSocketSession participant : participants) {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonMessage = objectMapper.writeValueAsString(socketMessage);
                participant.sendMessage(new TextMessage(jsonMessage));
            }
        }
    }

    private SocketPLO convertToChatMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(message, SocketPLO.class);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to convert json to chat message" + e.getMessage());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("The close is success");
        sessions.remove(session);
        for (List<WebSocketSession> chatParticipants : chatRooms.values()) {
            chatParticipants.remove(session);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
