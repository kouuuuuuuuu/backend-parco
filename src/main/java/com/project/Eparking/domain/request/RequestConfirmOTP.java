package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestConfirmOTP {
    private String OTPcode;
    private String role;
    private String phoneNumber;

    private String fullName;
    private String password;
}
