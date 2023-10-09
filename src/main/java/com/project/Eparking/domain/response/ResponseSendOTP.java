package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSendOTP {
    private String codeResult;
    private String smsid;
    private String errorMessage;
    private String countRegenerate;
}
