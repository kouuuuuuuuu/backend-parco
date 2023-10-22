package com.project.Eparking.controller;

import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerControllerRole {
    private final CustomerService customerService;
    @GetMapping("/getProfile")
    public ResponseEntity<ResponseCustomer> getProfile(){
        try{
            return ResponseEntity.ok(customerService.getResponseCustomerByCustomerID());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestBody RequestCustomerUpdateProfile profile){
        try {
            return ResponseEntity.ok(customerService.updateCustomerProfile(profile));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/updatePassword")
    private ResponseEntity<List<String>> updateNewPassword(@RequestBody RequestChangePasswordUser user){
        try {
            return ResponseEntity.ok(customerService.updatePassswordCustomer(user));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
