package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseNotifications {
    private int notiID;
    private String recipient_type;
    private String recipient_id;
    private String sender_type;
    private String senderName;
    private String content;
    private Timestamp created_at;
}
