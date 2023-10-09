package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCheckOTP {
    private String codeResult;
    private String countRegenerate;
    private String errorMessage;
    private String content;
}
