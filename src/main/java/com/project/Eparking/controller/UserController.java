package com.project.Eparking.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.request.LoginUser;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.LicensePlateService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if(username.startsWith("CU") || username.startsWith("cu")){
            try{
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getID(), loginUser.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String name = authentication.getName();
                Customer customer = userMapper.getCustomerByCustomerID(name);
                if(customer.getStatus() == 2){
                    String access_token = JWT.create()
                            .withSubject(customer.getCustomerID())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", customer.getRole())
                            .sign(algorithm);
                    ResponseCustomer responseCustomer = userMapper.getResponseCustomerByCustomerID(customer.getCustomerID());
                    List<LicensePlate> licensePlates = licensePlateService.getListLicensePlateByCustomerID(responseCustomer.getCustomerID());
                    responseCustomer.setLicensePlateList(licensePlates);
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
        } else if (username.startsWith("PL") ||username.startsWith("pl")) {
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
        } else if (username.startsWith("ad") || username.startsWith("AD")) {
            try{
                Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginUser.getID(), loginUser.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String name = authentication.getName();
                Admin admin = userMapper.getAdminByAdminID(name);
                if(admin.getStatus() == 2){
                    String access_token = JWT.create()
                            .withSubject(admin.getAdminID())
//                    .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", admin.getRole())
                            .sign(algorithm);
                    ResponseAdmin responseAdmin = userMapper.getAdminResponseByAdminID(admin.getAdminID());
                    Map<String, Object> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("Customer", responseAdmin);
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
    @GetMapping("/getResponse")
    public ResponseCustomer customer (String id){
        return userService.getResponseCustomerByCustomerID(id);
    }

}
