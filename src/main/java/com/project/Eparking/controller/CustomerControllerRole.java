package com.project.Eparking.controller;

import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerTransaction;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerControllerRole {
    private final CustomerService customerService;
    private final PaymentService paymentService;
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
    public ResponseEntity<List<String>> updateNewPassword(@RequestBody RequestChangePasswordUser user){
        try {
            return ResponseEntity.ok(customerService.updatePassswordCustomer(user));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/returnPayment")
    public ResponseEntity<?> returnPaymentCustomer(HttpServletRequest request){
        try {
            return paymentService.paymentReturnCustomer(request);
        }catch (ApiRequestException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/createPayment")
    public ResponseEntity<?> createPaymentCustomer(HttpServletRequest request, @RequestBody RequestCustomerTransaction transaction){
        try {
            return customerService.createPaymentCustomer(request,transaction);
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
