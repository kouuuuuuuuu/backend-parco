package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseToken {
    private int token;
    private String recipientType;
    private String recipientID;
    private String deviceToken;
}
