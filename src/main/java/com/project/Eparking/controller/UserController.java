package com.project.Eparking.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;
import com.project.Eparking.domain.response.ResponsePLOProfile;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.LicensePlateService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This customer is denied to login");
                }
            }catch (Exception e){
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
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
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This PLO is denied to login");
                }
            }catch (Exception e){
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
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
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
                else {
                    throw new ApiRequestException("This admin is denied to login");
                }
            }catch (Exception e){
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        }
    }
    @PostMapping("/regiterUser")
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
    @GetMapping("/PLO/profile")
    public ResponseEntity<ResponsePLOProfile> responsePLOProfile(){
        try{
            return ResponseEntity.ok(userService.getPLOProfileResponseByPLOID());
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/PLO/profile/update")
    public ResponseEntity<ResponsePLOProfile> updateProfilePLO(@RequestBody RequestPLOupdateProfile profile){
        try{
            return ResponseEntity.ok(userService.updatePLOprofile(profile));
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/changePassword")
    public ResponseEntity<List<String>> changePasswordUser(RequestChangePasswordUser password){
        try{
            return ResponseEntity.ok(userService.changePasswordUser(password));
        }catch (Exception e){
            throw e;
        }
    }
}
