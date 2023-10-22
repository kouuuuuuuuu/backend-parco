package com.project.Eparking.service.interf;

import com.project.Eparking.domain.Payment;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

public interface PaymentService {
    ResponseEntity<?> createPayment(HttpServletRequest req, Payment payment,String UUID) throws UnsupportedEncodingException;
    ResponseEntity<?> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException;
    ResponseEntity<?> createPaymentCustomer(HttpServletRequest req, Payment payment) throws UnsupportedEncodingException;
    ResponseEntity<?> paymentReturnCustomer(HttpServletRequest request) throws UnsupportedEncodingException;
}
