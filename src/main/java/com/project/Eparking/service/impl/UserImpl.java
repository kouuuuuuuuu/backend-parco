package com.project.Eparking.service.impl;

import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.ImageMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.ESMService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserService, UserDetailsService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ESMService esmService;
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;

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
                    throw new ApiRequestException("The PLO is already exists");
                }
                ResponseSendOTP responseSendOTP = esmService.sendOTP(user.getPhoneNumber());
                if(!responseSendOTP.getCodeResult().equals("100")){
                    throw new ApiRequestException("Can not send OTP to user");
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
                    throw new ApiRequestException("The Customer is already exists");
                }
                ResponseSendOTP responseSendOTP = esmService.sendOTP(user.getPhoneNumber());
                if(!responseSendOTP.getCodeResult().equals("100")){
                    throw new ApiRequestException("Can not send OTP to user");
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
                    throw new ApiRequestException("OTP code is invalid");
                }
            } else if (requestConfirmOTP.getRole().equalsIgnoreCase("PLO")) {
                ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestConfirmOTP.getPhoneNumber(), requestConfirmOTP.getOTPcode());
                if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                    requestConfirmOTP.setPassword(passwordEncoder.encode(requestConfirmOTP.getPassword()));
                    userMapper.createPLO(requestConfirmOTP,"PL"+requestConfirmOTP.getPhoneNumber(),0.0,2,1);
                    return "Successful register account";
                } else {
                    throw new ApiRequestException("OTP code is invalid");
                }
            }else{
                return "Cannot check OTP";
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to confirm password." + e.getMessage());
        }
    }
    @Override
    public String checkPhoneNumber(RequestForgotPassword requestForgotPassword) {
        String response = null;
        try{
            if(requestForgotPassword.getRole().equalsIgnoreCase("PLO")){
                ResponsePLO responsePLO = userMapper.getPLOResponseByPhonenumber(requestForgotPassword.getPhoneNumber());
                if(responsePLO == null){
                    throw new ApiRequestException("PLO is not exists!");
                }else{
                    ResponseSendOTP responseSendOTP = esmService.sendOTP(requestForgotPassword.getPhoneNumber());
                    if(!responseSendOTP.getCodeResult().equals("100")){
                        throw new ApiRequestException("Can not send OTP to user");
                    }
                    response =  "PLO is exists!";
                }
            } else if (requestForgotPassword.getRole().equalsIgnoreCase("CUSTOMER")) {
                ResponseCustomer responseCustomer = userMapper.getCustomerResponseByPhonenumber(requestForgotPassword.getPhoneNumber());
                if(responseCustomer == null){
                    throw new ApiRequestException("CUSTOMER is not exists!");
                }else {
                    ResponseSendOTP responseSendOTP = esmService.sendOTP(requestForgotPassword.getPhoneNumber());
                    if(!responseSendOTP.getCodeResult().equals("100")){
                        throw new ApiRequestException("Can not send OTP to user");
                    }
                    response = "CUSTOMER is exists!";
                }
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to check the phoneNumber." + e.getMessage());
        }
    }

    @Override
    public String forgotPasswordCheckOTP(RequestForgotPasswordOTPcode requestForgotPasswordOTPcode) {
        try{
            ResponseCheckOTP responseCheckOTP = esmService.checkOTP(requestForgotPasswordOTPcode.getPhoneNumber(), requestForgotPasswordOTPcode.getOTPcode());
            if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                return "OTP code is valid!";
            } else {
                throw new ApiRequestException("OTP code is invalid");
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to check the OTP code." + e.getMessage());
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


    @Override
    public ResponsePLOProfile updatePLOprofile(RequestPLOupdateProfile profile) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            userMapper.updatePLOprofile(profile,id);
            return userMapper.getPLOProfileResponseByPLOID(id);
        }catch (Exception e){
            throw new ApiRequestException("Failed to update PLO profile" + e.getMessage());
        }
    }

    @Override
    public List<String> changePasswordUser(RequestChangePasswordUser password) {
        List<String> response = new ArrayList<>();
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            boolean hasError = false;
            if(id.startsWith("cu") || id.startsWith("CU")){
                boolean check = true;
                Customer customer = userMapper.getCustomerByCustomerID(id);
                if(!passwordEncoder.matches(password.getCurrentPassword(),customer.getPassword())){
                    check = false;
                    hasError = true;
                    response.add("The current password is wrong!");
                }
                if (!password.getNewPassword().equals(password.getReNewPassword())) {
                    check = false;
                    hasError = true;
                    response.add("The new password is not match");
                }
                if(check){
                    RequestChangePassword requestChangePassword = new RequestChangePassword();
                    requestChangePassword.setPassword(passwordEncoder.encode(password.getNewPassword()));
                    userMapper.updateNewPasswordCustomer(requestChangePassword,id);
                    response.add("Update new password successfully");
                }

            } else if (id.startsWith("pl") || id.startsWith("PL")) {
                boolean check = true;
                PLO plo = userMapper.getPLOByPLOID(id);
                if(!passwordEncoder.matches(password.getCurrentPassword(),plo.getPassword())){
                    check = false;
                    hasError = true;
                    response.add("The current password is wrong!");
                }if (!password.getNewPassword().equals(password.getReNewPassword())) {
                    check = false;
                    hasError = true;
                    response.add("The new password is not match");
                }
                if(check){
                    RequestChangePassword requestChangePassword = new RequestChangePassword();
                    requestChangePassword.setPassword(passwordEncoder.encode(password.getNewPassword()));
                    userMapper.updateNewPasswordPLO(requestChangePassword,id);
                    response.add("Update new password successfully");
                }
            }
            if (hasError) {
                throw new ApiRequestException("Failed to change password user");
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to change password user." + e.getMessage());
        }
    }

    @Override
    public double getBalancePlO() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            return userMapper.getBalancePlO(id);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get plo balance by ID" + e.getMessage());
        }
    }

    @Override
    public List<ResponseNotifications> getListNotificationByID() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<Notifications> notifications = userMapper.getListNotificationByID(id);
            List<ResponseNotifications> listNotiRes = new ArrayList<>();
            for (Notifications notis:
                 notifications) {
                if(notis.getSender_type().equalsIgnoreCase("ADMIN")){
                    listNotiRes.add(new ResponseNotifications(notis.getNotiID(), notis.getRecipient_type(),notis.getRecipient_id(),notis.getSender_type(),"ADMIN", notis.getContent(), notis.getCreated_at(),notis.getImageLink()));
                }else if(notis.getSender_type().equalsIgnoreCase("CUSTOMER")){
                    Customer customer = userMapper.getCustomerByCustomerID(notis.getSender_id());
                    listNotiRes.add(new ResponseNotifications(notis.getNotiID(), notis.getRecipient_type(),notis.getRecipient_id(),notis.getSender_type(),customer.getFullName(), notis.getContent(), notis.getCreated_at(),notis.getImageLink()));
                } else if (notis.getSender_type().equalsIgnoreCase("PLO")) {
                    PLO plo = userMapper.getPLOByPLOID(notis.getSender_id());
                    listNotiRes.add(new ResponseNotifications(notis.getNotiID(), notis.getRecipient_type(),notis.getRecipient_id(),notis.getSender_type(),plo.getFullName(), notis.getContent(), notis.getCreated_at(), notis.getImageLink()));
                }

            }
            return listNotiRes;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get notifications by ID" + e.getMessage());
        }
    }
    @Override
    public ResponseProfile profileUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseProfile responseProfile = new ResponseProfile();
            if(id.startsWith("CU") ||id.startsWith("cu")){
                Customer responseCustomer = customerMapper.getCustomerById(id);
                responseProfile.setFullName(responseCustomer.getFullName());
                responseProfile.setPhoneNumber(responseCustomer.getPhoneNumber());
                responseProfile.setEmail(responseCustomer.getEmail());
            }else if(id.startsWith("PL") ||id.startsWith("pl")){
                PLO responsePLOProfile = userMapper.getPLOByPLOID(id);
                responseProfile.setFullName(responsePLOProfile.getFullName());
                responseProfile.setEmail(responsePLOProfile.getEmail());
                responseProfile.setPhoneNumber(responsePLOProfile.getPhoneNumber());
            }else {
                throw new ApiRequestException("Something wrong to get user profile");
            }
            return responseProfile;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get user profile" + e.getMessage());
        }
    }

    @Override
    public String updateProfile(RequestUpdateProfile requestUpdateProfile) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            String response;
            if (id.startsWith("CU") || id.startsWith("cu")) {
                RequestCustomerUpdateProfile updateProfile = new RequestCustomerUpdateProfile(requestUpdateProfile.getFullName(), requestUpdateProfile.getEmail());
                customerMapper.updateCustomerProfile(updateProfile, id);
                response = "Update profile customer successfully!";
            } else if (id.startsWith("PL") || id.startsWith("pl")) {
                RequestPLOupdateProfile profile = new RequestPLOupdateProfile(requestUpdateProfile.getFullName(), requestUpdateProfile.getEmail());
                userMapper.updatePLOprofile(profile, id);
                response = "Update profile PLO successfully!";
            } else {
                throw new ApiRequestException("Something wrong to update profile");
            }
            return response;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update user profile" + e.getMessage());
        }
    }
}
