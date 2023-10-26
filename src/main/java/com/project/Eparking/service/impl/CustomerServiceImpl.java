package com.project.Eparking.service.impl;

import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.CustomerTransactionMapper;
import com.project.Eparking.dao.LicensePlateMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.dto.CustomerWalletDTO;
import com.project.Eparking.domain.dto.CustomerWalletDTO;
import com.project.Eparking.domain.request.RequestChangePassword;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerTransaction;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final LicensePlateMapper licensePlateMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PaymentService paymentService;
    private final CustomerTransactionMapper transactionMapper;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    public Page<CustomerDTO> getListCustomer(int pageNum, int pageSize) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list customer from database
        List<Customer> listCustomerEntity = customerMapper.getListCustomer(pageNumOffset, pageSize);
        if (listCustomerEntity.isEmpty()){
            return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, 0);
        }
        //2. Mapping customer data to customer DTO;
        for (Customer c : listCustomerEntity){
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(c.getCustomerID());
            customerDTO.setPhoneNumber(c.getPhoneNumber());
            customerDTO.setFullName(c.getFullName());
            customerDTO.setStatus(c.getStatus() == 0 ? "Online" : "Offline");
            String registrationDate = dateFormat.format(c.getRegistrationDate());
            customerDTO.setRegistrationDate(registrationDate);

            //2.1. Get licensePlate total number by list customerId;
            List<LicensePlate> licensePlateList = licensePlateMapper.getListLicensePlateByCustomerID(c.getCustomerID());
            customerDTO.setTotalNumber(licensePlateList.size());
            customerDTOList.add(customerDTO);
        }

        int totalRecords = this.countRecords("");

        return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, totalRecords);
    }

    @Override
    public Page<CustomerDTO> getListCustomerByName(String name, int pageNum, int pageSize) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list customer from database
        String searchKeyword =name.trim();
        List<Customer> listCustomers = customerMapper.getListCustomerByName(searchKeyword, pageNumOffset, pageSize);
        if(listCustomers.isEmpty()){
            return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, 0);
        }

        //2. Mapping to DTO data
        for (Customer c : listCustomers){
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(c.getCustomerID());
            customerDTO.setPhoneNumber(c.getPhoneNumber());
            customerDTO.setFullName(c.getFullName());
            customerDTO.setStatus(c.getStatus() == 0 ? "Online" : "Offline");
            String registrationDate = dateFormat.format(c.getRegistrationDate());
            customerDTO.setRegistrationDate(registrationDate);

            //2.1. Get licensePlate total number by list customerId;
            List<LicensePlate> licensePlateList = licensePlateMapper.getListLicensePlateByCustomerID(c.getCustomerID());
            customerDTO.setTotalNumber(licensePlateList.size());
            customerDTOList.add(customerDTO);
        }

        int totalRecords = this.countRecords(searchKeyword);

        return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, totalRecords);
    }

    private Integer countRecords(String keyword){
        return customerMapper.countRecords(keyword);
    }

    @Override
    public ResponseCustomer getResponseCustomerByCustomerID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            return userMapper.getResponseCustomerByCustomerID(id);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get customer profile" +e.getMessage());
        }
    }

    @Override
    public String updateCustomerProfile(RequestCustomerUpdateProfile profile) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            customerMapper.updateCustomerProfile(profile,id);
            return "Update profile successfully";
        }catch (Exception e){
            throw new ApiRequestException("Failed to update customer profile" +e.getMessage());
        }
    }

    @Override
    public List<String> updatePassswordCustomer(RequestChangePasswordUser customerParam) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<String> response = new ArrayList<>();
            boolean check = true;
            Customer customer = userMapper.getCustomerByCustomerID(id);
            if(!passwordEncoder.matches(customerParam.getCurrentPassword(),customer.getPassword())){
                check = false;
                response.add("The current password is wrong!");
            }
            if (!customerParam.getNewPassword().equals(customerParam.getReNewPassword())) {
                check = false;
                response.add("The new password is not match");
            }
            if(check){
                customerMapper.updatePasswordCustomer(passwordEncoder.encode(customerParam.getNewPassword()),id);
                response.add("Update new password successfully");
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to update password customer" +e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> createPaymentCustomer(HttpServletRequest req,RequestCustomerTransaction transaction) {
       try{
           Payment payment = new Payment();
           payment.setAmountParam(String.valueOf(transaction.getAmount()));
           return paymentService.createPaymentCustomer(req,payment);
       }catch (Exception e){
           throw new ApiRequestException("Failed to create payment customer" +e.getMessage());
       }
    }

    @Override
    public ResponseWalletScreen responseWalletScreen() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseWalletScreen responseWalletScreen = new ResponseWalletScreen();
            responseWalletScreen.setWallet_balance(userMapper.getCustomerByCustomerID(id).getWalletBalance());
            responseWalletScreen.setHistoryBalanceCustomerList(transactionMapper.getListTransactionCustomer(id));
            return responseWalletScreen;
        }catch (Exception e){
            throw new ApiRequestException("Failed to response wallet screen" +e.getMessage());
        }
    }

    @Override
    public List<WeekData> countRecordsByWeekCustomer(RequestMonthANDYear requestMonthANDYear) {
        try {
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            Response4week week = customerMapper.countRecordsByWeekCustomer(sqlDate);
            List<WeekData> result = new ArrayList<>();
            result.add(new WeekData("Tuần 1", week.getWeek1()));
            result.add(new WeekData("Tuần 2", week.getWeek2()));
            result.add(new WeekData("Tuần 3", week.getWeek3()));
            result.add(new WeekData("Tuần 4", week.getWeek4()));
            return result;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get chart week for customer" +e.getMessage());
        }
    }

//    @Override
//    public CustomerWalletDTO getCustomerBalance() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String id = authentication.getName();
//        Customer customer = customerMapper.getCustomerBalance(id);
//        CustomerWalletDTO customerWalletDTO = new CustomerWalletDTO();
////        customerWalletDTO.setCustomerID(customer.getCustomerID());
//        customerWalletDTO.setWallet_balance(customer.getWalletBalance());
//        return  customerWalletDTO;
//    }

    @Override
    public CustomerWalletDTO getCustomerBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Customer customer = customerMapper.getCustomerBalance(id);
        CustomerWalletDTO customerWalletDTO = new CustomerWalletDTO();
//        customerWalletDTO.setCustomerID(customer.getCustomerID());
        customerWalletDTO.setWallet_balance(customer.getWalletBalance());
        return  customerWalletDTO;
    }
}
