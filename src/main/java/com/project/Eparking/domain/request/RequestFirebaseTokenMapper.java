package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFirebaseTokenMapper {
    private String recipientType;
    private String recipientID;
    private String deviceToken;
}
