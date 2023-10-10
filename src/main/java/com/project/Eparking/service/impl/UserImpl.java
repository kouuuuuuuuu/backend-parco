package com.project.Eparking.service.impl;

import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ESMService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserService, UserDetailsService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ESMService esmService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.startsWith("c") || username.startsWith("C")){
            Customer customer = userMapper.getCustomerByCustomerID(username);
            if(customer == null){
                log.error("Customer not found in the database");
                throw new UsernameNotFoundException("Customer not found in the database");
            }else{
                log.info("Customer found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(customer.getRole()));
            return new org.springframework.security.core.userdetails.User(customer.getCustomerID(), customer.getPassword(), authorities);
        }else if (username.startsWith("p") ||username.startsWith("P")){
            PLO plo = userMapper.getPLOByPLOID(username);
            if(plo == null){
                log.error("plo not found in the database");
                throw new UsernameNotFoundException("plo not found in the database");
            }else{
                log.info("plo found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(plo.getRole()));
            return new org.springframework.security.core.userdetails.User(plo.getPloID(), plo.getPassword(), authorities);
        }else if (username.startsWith("a") ||username.startsWith("A")){
            Admin Admin = userMapper.getAdminByAdminID(username);
            if(Admin == null){
                log.error("Admin not found in the database");
                throw new UsernameNotFoundException("Admin not found in the database");
            }else{
                log.info("Admin found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(Admin.getRole()));
            return new org.springframework.security.core.userdetails.User(Admin.getAdminID(), Admin.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
    @Override
    public Admin getAdminByAdminID(String adminID) {
        try {
            return userMapper.getAdminByAdminID(adminID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Admin by AdminID" +e.getMessage());
        }
    }
    @Override
    public Customer getCustomerByCustomerID(String customerID) {
        try {
            return userMapper.getCustomerByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Customer by customerID" +e.getMessage());
        }
    }

    @Override
    public PLO getPLOByPLOID(String ploID) {
        try {
            return userMapper.getPLOByPLOID(ploID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get PLO by PLOID" +e.getMessage());
        }
    }

    @Override
    public ResponseCustomer getResponseCustomerByCustomerID(String customerID) {
        try {
            return userMapper.getResponseCustomerByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Customer Response by customerID" +e.getMessage());
        }
    }

    @Override
    public ResponsePLO getPLOResponseByPLOID(String ploID) {
        try {
            return userMapper.getPLOResponseByPLOID(ploID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get PLO Response by PLOID" +e.getMessage());
        }
    }

    @Override
    public ResponseAdmin getAdminResponseByAdminID(String adminID) {
        try{
            return userMapper.getAdminResponseByAdminID(adminID);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get Admin Response by AdminID" +e.getMessage());
        }
    }

    @Override
    public String registerPLO(RequestRegisterUser user) {
        String response = "";
        if(user.getRole().equalsIgnoreCase("PLO")){
            try{
                ResponsePLO existingPLO = userMapper.getPLOResponseByPhonenumber(user.getPhoneNumber());
                if(existingPLO != null){
                    return response = "The PLO is already exists";
                }
                ResponseSendOTP responseSendOTP = esmService.sendOTP(user.getPhoneNumber());
                if(!responseSendOTP.getCodeResult().equals("100")){
                    return response = "Can not send OTP to user";
                }
                response = "Send OTP successfully";
            }catch (Exception e){
                throw new ApiRequestException("Failed to send OTP to phoneNumber plo: " + e.getMessage());
            }
        }
        return response;
    }

    @Override
    public String registerCustomer(RequestRegisterUser user) {
        String response = "";
        if(user.getRole().equalsIgnoreCase("CUSTOMER")){
            try{
                ResponseCustomer existingCustomer = userMapper.getCustomerResponseByPhonenumber(user.getPhoneNumber());
                if(existingCustomer != null){
                    return response = "The Customer is already exists";
                }
                ResponseSendOTP responseSendOTP = esmService.sendOTP(user.getPhoneNumber());
                if(!responseSendOTP.getCodeResult().equals("100")){
                    return response = "Can not send OTP to user";
                }
                response = "Send OTP successfully";
            }catch (Exception e){
                throw new ApiRequestException("Failed to send OTP to customer: " + e.getMessage());
            }
        }
        return response;
    }

    @Override
    public String registerConfirmOTPcode(RequestConfirmOTP requestConfirmOTP) {
        try{
            if(requestConfirmOTP.getRole().equalsIgnoreCase("CUSTOMER")){
                ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestConfirmOTP.getPhoneNumber(), requestConfirmOTP.getOTPcode());
                if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                    requestConfirmOTP.setPassword(passwordEncoder.encode(requestConfirmOTP.getPassword()));
                    userMapper.createCustomer(requestConfirmOTP,"CU"+requestConfirmOTP.getPhoneNumber(),0.0,2);
                    return "Successful register account";
                } else {
                    return "OTP code is invalid";
                }
            } else if (requestConfirmOTP.getRole().equalsIgnoreCase("PLO")) {
                ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestConfirmOTP.getPhoneNumber(), requestConfirmOTP.getOTPcode());
                if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                    requestConfirmOTP.setPassword(passwordEncoder.encode(requestConfirmOTP.getPassword()));
                    userMapper.createPLO(requestConfirmOTP,"PL"+requestConfirmOTP.getPhoneNumber(),0.0,2,1);
                    return "Successful register account";
                } else {
                    return "OTP code is invalid";
                }
            }else{
                return "Cannot check OTP";
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to confirm password" + e.getMessage());
        }
    }
    @Override
    public String checkPhoneNumber(RequestForgotPassword requestForgotPassword) {
        String response = null;
        try{
            if(requestForgotPassword.getRole().equalsIgnoreCase("PLO")){
                ResponsePLO responsePLO = userMapper.getPLOResponseByPhonenumber(requestForgotPassword.getPhoneNumber());
                if(responsePLO == null){
                    response =  "PLO is not exists!";
                }else{
                    ResponseSendOTP responseSendOTP = esmService.sendOTP(requestForgotPassword.getPhoneNumber());
                    if(!responseSendOTP.getCodeResult().equals("100")){
                        return response = "Can not send OTP to user";
                    }
                    response =  "PLO is exists!";
                }
            } else if (requestForgotPassword.getRole().equalsIgnoreCase("CUSTOMER")) {
                ResponseCustomer responseCustomer = userMapper.getCustomerResponseByPhonenumber(requestForgotPassword.getPhoneNumber());
                if(responseCustomer == null){
                    response =  "CUSTOMER is not exists!";
                }else {
                    ResponseSendOTP responseSendOTP = esmService.sendOTP(requestForgotPassword.getPhoneNumber());
                    if(!responseSendOTP.getCodeResult().equals("100")){
                        return response = "Can not send OTP to user";
                    }
                    response = "CUSTOMER is exists!";
                }
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to check the phoneNumber" + e.getMessage());
        }
    }

    @Override
    public String forgotPasswordCheckOTP(RequestForgotPasswordOTPcode requestForgotPasswordOTPcode) {
        try{
            ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestForgotPasswordOTPcode.getPhoneNumber(), requestForgotPasswordOTPcode.getOTPcode());
            if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                return "OTP code is valid!";
            } else {
                return "OTP code is invalid";
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to check the OTP code" + e.getMessage());
        }
    }

    @Override
    public String updatePasswordUser(RequestChangePassword password) {
        String response = "Failed to update new password";
        try{
            if(password.getRole().equalsIgnoreCase("CUSTOMER")){
                password.setPassword(passwordEncoder.encode(password.getPassword()));
                userMapper.updateNewPasswordCustomer(password,"CU"+password.getPhoneNumber());
                response = "update new password for customer successfully!";
            } else if (password.getRole().equalsIgnoreCase("PLO")) {
                password.setPassword(passwordEncoder.encode(password.getPassword()));
                userMapper.updateNewPasswordPLO(password,"PL"+password.getPhoneNumber());
                response = "update new password for PLO successfully!";
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to update new password" + e.getMessage());
        }
    }
}
