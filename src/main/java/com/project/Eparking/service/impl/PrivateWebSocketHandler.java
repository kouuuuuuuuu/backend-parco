package com.project.Eparking.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PrivateWebSocketHandler implements WebSocketHandler {
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

        String roomCode = extractRoomCode((String) clientValue);
        String content = extractRoomMessage((String) clientValue);

        List<WebSocketSession> chatParticipants = chatRooms.computeIfAbsent(roomCode, k -> new ArrayList<>());

        if (!chatParticipants.contains(session)) {
            chatParticipants.add(session);
        }

        System.out.println(content);

        for (WebSocketSession participant : chatParticipants) {
            participant.sendMessage(new TextMessage(content));
        }
    }

    private String extractRoomCode(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            return jsonNode.get("chatId").asText();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractRoomMessage(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);
            return jsonNode.get("message").asText();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("The close is success");
        sessions.remove(session);

        // Loại bỏ phiên khỏi tất cả các cuộc trò chuyện
        for (List<WebSocketSession> chatParticipants : chatRooms.values()) {
            chatParticipants.remove(session);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
