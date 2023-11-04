package com.project.Eparking.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.PushNotificationService;
import com.project.Eparking.service.impl.PrivateWebSocketHandler;
import com.project.Eparking.service.interf.FirebaseTokenService;
import com.project.Eparking.service.interf.LicensePlateService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final UserService userService;
    private final LicensePlateService licensePlateService;
    private final PushNotificationService pushNotificationService;
    private final FirebaseTokenService firebaseTokenService;


    @Value("${SECRET_KEY}")
    private String secret;
    @PostMapping("/loginUser")
    public void loginUser(@RequestBody LoginUser loginUser,
                          HttpServletResponse response,
                          HttpServletRequest request) throws IOException {
        String username = loginUser.getID();
        if(username.startsWith("C") || username.startsWith("c")){
            try{
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getID(), loginUser.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String name = authentication.getName();
                Customer customer = userService.getCustomerByCustomerID(name);
                if(customer.getStatus() == 2){
                    String access_token = JWT.create()
                            .withSubject(customer.getCustomerID())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", customer.getRole())
                            .sign(algorithm);
                    ResponseCustomer responseCustomer = userService.getResponseCustomerByCustomerID(customer.getCustomerID());
                    Map<String, Object> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("Customer", responseCustomer);
                    boolean isAdmin = false;
                    tokens.put("isAdmin",isAdmin);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This customer is denied to login");
                }
            }catch (Exception e){
                String cleanedErrorMessage = cleanString(e.getMessage());
                response.setHeader("error", cleanedErrorMessage);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", cleanedErrorMessage);
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getOutputStream(), error);
            }
        } else if (username.startsWith("P") ||username.startsWith("p")) {
            try{
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getID(), loginUser.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String name = authentication.getName();
                PLO plo = userService.getPLOByPLOID(name);
                if(plo.getStatus() == 2){
                    String access_token = JWT.create()
                            .withSubject(plo.getPloID())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", plo.getRole())
                            .sign(algorithm);
                    ResponsePLO responsePLO= userService.getPLOResponseByPLOID(plo.getPloID());
                    Map<String, Object> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("PLO", responsePLO);
                    boolean isAdmin = false;
                    tokens.put("isAdmin",isAdmin);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This PLO is denied to login");
                }
            }catch (Exception e){
                String cleanedErrorMessage = cleanString(e.getMessage());
                response.setHeader("error", cleanedErrorMessage);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", cleanedErrorMessage);
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getOutputStream(), error);
            }
        } else if (username.startsWith("a") || username.startsWith("A")) {
            try{
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getID(), loginUser.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String name = authentication.getName();
                Admin admin = userService.getAdminByAdminID(name);
                if(admin.getStatus() == 2){
                    String access_token = JWT.create()
                            .withSubject(admin.getAdminID())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", admin.getRole())
                            .sign(algorithm);
                    ResponseAdmin responseAdmin = userService.getAdminResponseByAdminID(admin.getAdminID());
                    Map<String, Object> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("Admin", responseAdmin);
                    boolean isAdmin = true;
                    tokens.put("isAdmin",isAdmin);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This admin is denied to login");
                }
            }catch (Exception e){
                String cleanedErrorMessage = cleanString(e.getMessage());
                response.setHeader("error", cleanedErrorMessage);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", cleanedErrorMessage);
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writeValue(response.getOutputStream(), error);
            }
        }else {
            // Invalid user ID prefix
            response.setHeader("error", "Invalid user ID prefix");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", "Invalid user ID prefix");
            response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getOutputStream(), error);
        }
    }
    private String cleanString(String input) {
        String cleaned = input.replaceAll("[\r\n]", "");
        return cleaned;
    }
    @PostMapping("/registerUser")
    public ResponseEntity<String> registerUser(RequestRegisterUser user){
        try{
            if(user.getRole().equalsIgnoreCase("PLO")){
                String responsePLO = userService.registerPLO(user);
                if(responsePLO.equals("")){
                    return ResponseEntity.ok("Register failed");
                }
                return ResponseEntity.ok(responsePLO);
            }
            else if(user.getRole().equalsIgnoreCase("CUSTOMER")){
                String responseCustomer = userService.registerCustomer(user);
                if(responseCustomer.equals("")){
                    return ResponseEntity.ok("Register failed");
                }
                return ResponseEntity.ok(responseCustomer);
            }
            return ResponseEntity.ok("Send OTP completed");
        }catch (Exception e){
            throw e;
        }
    }
    @PostMapping("/confirmRegisterOTP")
    public ResponseEntity<String> confirmOTPassword(@RequestBody RequestConfirmOTP requestConfirmOTP) throws IOException {
        try {
            return ResponseEntity.ok(userService.registerConfirmOTPcode(requestConfirmOTP));
        }catch (Exception e){
            throw e;
        }
    }
    @PostMapping("/checkPhoneNumber")
    public ResponseEntity<String> checkPhoneNumber(@RequestBody RequestForgotPassword requestForgotPassword){
        try{
            return  ResponseEntity.ok(userService.checkPhoneNumber(requestForgotPassword));
        }catch (Exception e){
            throw e;
        }
    }
    @PostMapping("/checkOTPcode")
    public ResponseEntity<String> checkOTPcode(@RequestBody RequestForgotPasswordOTPcode requestForgotPasswordOTPcode) {
        try {
            return ResponseEntity.ok(userService.forgotPasswordCheckOTP(requestForgotPasswordOTPcode));
        } catch (Exception e) {
            throw e;
        }
    }
    @PutMapping("/updatePassword")
    public ResponseEntity<String> updateNewPassword(@RequestBody RequestChangePassword password){
        try{
            return ResponseEntity.ok(userService.updatePasswordUser(password));
        }catch (Exception e){
            throw e;
        }
    }
    @GetMapping("/getNotifcations")
    public ResponseEntity<List<ResponseNotifications>> getListNotification(){
        try {
            return ResponseEntity.ok(userService.getListNotificationByID());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PostMapping("/addDeviceToken")
    public ResponseEntity<String> addDeviceToken(@RequestBody RequestFirebaseToken firebaseToken){
        try {
            return ResponseEntity.ok(firebaseTokenService.addDeviceToken(firebaseToken));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @DeleteMapping("/deleteDeviceToken")
    public ResponseEntity<String> deleteDeviceToken(@RequestParam String deviceToken){
        try {
            return ResponseEntity.ok(firebaseTokenService.deleteTokenByTokenDevice(deviceToken));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PostMapping("/sendNoti")
    public ResponseEntity sendNoti(@RequestBody PushNotificationRequest request){
        try {
            pushNotificationService.sendPushNotificationToToken(request);
            return new ResponseEntity<>(new PushNotificationResponse(HttpStatus.OK.value(), "Noti has sent"),HttpStatus.OK);
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getProfileUser")
    public ResponseEntity<ResponseProfile> getProfileUser(){
        try {
            return ResponseEntity.ok(userService.profileUser());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/updateProfileUser")
    public ResponseEntity<String> updateProfileUser(@RequestBody RequestUpdateProfile profile){
        try {
            return ResponseEntity.ok(userService.updateProfile(profile));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/changePassword")
    public ResponseEntity<List<String>> changePasswordUser(@RequestBody RequestChangePasswordUser password){
        try {
            return ResponseEntity.ok(userService.changePasswordUser(password));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
