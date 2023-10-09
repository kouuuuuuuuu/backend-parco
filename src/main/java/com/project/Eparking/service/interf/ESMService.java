package com.project.Eparking.service.interf;

import com.project.Eparking.domain.response.ResponseCheckOTP;
import com.project.Eparking.domain.response.ResponseSendOTP;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public interface ESMService {
    public ResponseSendOTP sendOTP(String phoneNumber) throws IOException;
    public ResponseCheckOTP checkOTP(String phoneNumber, String OTPcode) throws IOException;
}
