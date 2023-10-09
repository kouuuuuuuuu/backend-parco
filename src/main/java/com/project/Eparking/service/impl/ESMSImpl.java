package com.project.Eparking.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.domain.response.ResponseCheckOTP;
import com.project.Eparking.domain.response.ResponseSendOTP;
import com.project.Eparking.service.interf.ESMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
@Service
@RequiredArgsConstructor
@Slf4j
public class ESMSImpl implements ESMService {
    @Value("${APIKey}")
    private String APIKey;
    @Value("${SecretKey}")
    private String SecretKey;
    @Override
    public ResponseSendOTP sendOTP(String phoneNumber) throws IOException {
        String encodedPhone = URLEncoder.encode(phoneNumber, "UTF-8");
        String encodedMessage = URLEncoder.encode("{OTP} la ma xac minh dang ky Baotrixemay cua ban", "UTF-8");
        String url = "http://rest.esms.vn/MainService.svc/json/SendMessageAutoGenCode_V4_get?Phone="+encodedPhone+"&ApiKey="+APIKey+"&SecretKey="+SecretKey+"&TimeAlive=3&NumCharOfCode=4&Brandname=Baotrixemay&Type=2&message="+encodedMessage+"&IsNumber=1";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseCodeResult = "";
            String responseCountRegenerate = "";
            String responseSMSID = "";
            String responseErrorMessage = "";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            responseCodeResult = rootNode.get("CodeResult").asText();
            responseCountRegenerate = rootNode.get("CountRegenerate").asText();
            responseSMSID = rootNode.get("SMSID").asText();

            ResponseSendOTP responseOTP = new ResponseSendOTP();
            responseOTP.setCodeResult(responseCodeResult);
            responseOTP.setCountRegenerate(responseCountRegenerate);
            responseOTP.setSmsid(responseSMSID);
            responseOTP.setErrorMessage(responseErrorMessage);

            return responseOTP;
        } else {
            throw new IOException("Error response code: " + responseCode);
        }
    }

    @Override
    public ResponseCheckOTP checkOTP(String phoneNumber, String OTPcode) throws IOException {
        String encodedPhone = URLEncoder.encode(phoneNumber, "UTF-8");
        String encodedOTP = URLEncoder.encode(OTPcode, "UTF-8");
        String url = "http://rest.esms.vn/MainService.svc/json/CheckCodeGen_V4_get?Phone="+encodedPhone+
                "&ApiKey="+URLEncoder.encode(APIKey, "UTF-8")+
                "&SecretKey="+URLEncoder.encode(SecretKey, "UTF-8")+
                "&Code="+encodedOTP;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            // Proceed with further processing
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String codeResult = "";
            String countRegenerate = "";
            String errorMessage = "";
            String content = "";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            ResponseCheckOTP responseCheckOTP = new ResponseCheckOTP();
            responseCheckOTP.setCodeResult(rootNode.get("CodeResult").asText());
            responseCheckOTP.setCountRegenerate(rootNode.get("CountRegenerate").asText());
            return responseCheckOTP;
        } else {
            throw new IOException("Error response code: " + responseCode);
        }
    }
}
