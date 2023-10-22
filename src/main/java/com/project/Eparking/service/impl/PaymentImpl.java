package com.project.Eparking.service.impl;

import com.project.Eparking.config.VNpayConfig;
import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.CustomerTransactionMapper;
import com.project.Eparking.dao.TransactionMapper;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.TransactionMethod;
import com.project.Eparking.domain.request.RequestCustomerTransactionMapper;
import com.project.Eparking.domain.request.RequestPLOTransaction;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.PaymentService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentImpl implements PaymentService {
    private final UserService userService;
    private final TransactionMapper transactionMapper;
    private final CustomerTransactionMapper transactionMapperCustomer;
    private final CustomerMapper customerMapper;
    private final String urlResponseCustomer= "https://eparking.azurewebsites.net/customer/returnPayment";
    public ResponseEntity<?> createPayment(HttpServletRequest req, Payment payment,String UUID) throws UnsupportedEncodingException {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            long amount =  Integer.parseInt(payment.getAmountParam()) * 100;
            String bankCode = "VNBANK";

            String vnp_TxnRef = VNpayConfig.getRandomNumber(8);
            String vnp_IpAddr = VNpayConfig.getIpAddress(req);
            String vnp_TmnCode = VNpayConfig.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");

            if (payment.getBackCode() == null || payment.getBackCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }else{
                vnp_Params.put("vnp_BankCode", payment.getBackCode());
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            String orderInfor = UUID + "+" +id;

            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfor);
            vnp_Params.put("vnp_OrderType", "other");

            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", VNpayConfig.vnp_Returnurl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNpayConfig.hmacSHA512(VNpayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNpayConfig.vnp_PayUrl + "?" + queryUrl;

            Map<String, String> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("url", paymentUrl);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            throw new ApiRequestException("Fail to create payment action: " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public ResponseEntity<?> paymentReturn(HttpServletRequest request) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNpayConfig.hashAllFields(fields);

        RequestPLOTransaction transactionData = new RequestPLOTransaction();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        if (signValue.equals(request.getParameter("vnp_SecureHash"))) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                try {
                    LocalDateTime paymentDateTime = LocalDateTime.parse(request.getParameter("vnp_PayDate"), formatter);
                    Timestamp timestamp = Timestamp.valueOf(paymentDateTime);
                    String UUIDandID = request.getParameter("vnp_OrderInfo");

                    String[] parts = UUIDandID.split("\\+");

                    transactionData.setPloID(parts[1]);

                    transactionData.setUUID(parts[0]);
                    transactionData.setVnPay_ref(request.getParameter("vnp_TxnRef"));

                    transactionData.setDepositAmount(Double.parseDouble(request.getParameter("vnp_Amount")) / 100);
                    transactionData.setTransactionDate(timestamp);

                    long currentTimeMillis = System.currentTimeMillis();
                    Timestamp currentTimestamp = new Timestamp(currentTimeMillis);

                    transactionData.setTransactionResultDate(currentTimestamp);
                    transactionData.setStatus(1);

                    transactionMapper.insertTransactionPLO(transactionData);
                    PLOTransaction ploTransaction = transactionMapper.getTransactionByUUID(parts[0]);
                    TransactionMethod transactionMethod = new TransactionMethod();

                    transactionMethod.setBankCode(request.getParameter("vnp_BankCode"));
                    transactionMethod.setHistoryID(ploTransaction.getHistoryID());

                    transactionMapper.insertTransactionMethod(transactionMethod);
                }catch (ApiRequestException e){
                    return ResponseEntity.ok("Payment handled failed");
                }
                return ResponseEntity.ok("Payment handled successfully");
            } else {
                return ResponseEntity.ok("Payment handled failed");
            }
        } else {
            return ResponseEntity.ok("Payment handled failed 0");
        }
    }
    @Override
    public ResponseEntity<?> createPaymentCustomer(HttpServletRequest req, Payment payment) throws UnsupportedEncodingException {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            long amount =  Integer.parseInt(payment.getAmountParam()) * 100;
            String bankCode = "VNBANK";

            String vnp_TxnRef = VNpayConfig.getRandomNumber(8);
            String vnp_IpAddr = VNpayConfig.getIpAddress(req);
            String vnp_TmnCode = VNpayConfig.vnp_TmnCode;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");

            if (payment.getBackCode() == null || payment.getBackCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", bankCode);
            }else{
                vnp_Params.put("vnp_BankCode", payment.getBackCode());
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();

            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", id);
            vnp_Params.put("vnp_OrderType", "other");

            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", urlResponseCustomer);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNpayConfig.hmacSHA512(VNpayConfig.vnp_HashSecret, hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = VNpayConfig.vnp_PayUrl + "?" + queryUrl;

            Map<String, String> response = new HashMap<>();
            response.put("code", "00");
            response.put("message", "success");
            response.put("url", paymentUrl);
            return ResponseEntity.ok().body(response);
        }catch (Exception e){
            throw new ApiRequestException("Fail to create payment action: " + e.getMessage());
        }
    }
    @Transactional
    @Override
    public ResponseEntity<?> paymentReturnCustomer(HttpServletRequest request) throws UnsupportedEncodingException {
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNpayConfig.hashAllFields(fields);

        RequestCustomerTransactionMapper customerTransactionMapper = new RequestCustomerTransactionMapper();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        if (signValue.equals(request.getParameter("vnp_SecureHash"))) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                try {
                    LocalDateTime paymentDateTime = LocalDateTime.parse(request.getParameter("vnp_PayDate"), formatter);
                    Timestamp timestamp = Timestamp.valueOf(paymentDateTime);
                    customerTransactionMapper.setCustomerID(request.getParameter("vnp_OrderInfo"));
                    customerTransactionMapper.setVnPay_ref(request.getParameter("vnp_TxnRef"));
                    customerTransactionMapper.setDepositAmount(Double.parseDouble(request.getParameter("vnp_Amount")) / 100);
                    customerTransactionMapper.setRechargeTime(timestamp);
                    customerTransactionMapper.setBankCode(request.getParameter("vnp_BankCode"));
                    transactionMapperCustomer.insertCustomerTransaction(customerTransactionMapper);
                    Customer customer = userService.getCustomerByCustomerID(request.getParameter("vnp_OrderInfo"));
                    Double amount = Double.parseDouble(request.getParameter("vnp_Amount")) / 100;
                    Double balance = customer.getWalletBalance() + amount;
                    customerMapper.updateBalance(request.getParameter("vnp_OrderInfo"),balance);
                }catch (ApiRequestException e){
                    return ResponseEntity.ok("Payment handled failed");
                }
                return ResponseEntity.ok("Payment handled successfully");
            } else {
                return ResponseEntity.ok("Payment handled failed");
            }
        } else {
            return ResponseEntity.ok("Payment handled failed 0");
        }
    }
}
