package com.project.Eparking.dao;

import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.request.RequestChangePassword;
import com.project.Eparking.domain.request.RequestConfirmOTP;
import com.project.Eparking.domain.request.RequestRegisterUser;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;
import com.project.Eparking.domain.response.ResponseSendOTP;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    Admin getAdminByAdminID(String adminID);
    Customer getCustomerByCustomerID (String customerID);
    PLO getPLOByPLOID(String ploID);
    ResponseCustomer getResponseCustomerByCustomerID(String customerID);
    ResponsePLO getPLOResponseByPLOID(String ploID);
    ResponseAdmin getAdminResponseByAdminID(String adminID);
    ResponseCustomer getCustomerResponseByPhonenumber(String phoneNumber);
    ResponsePLO getPLOResponseByPhonenumber(String phoneNumber);
    void createPLO(RequestConfirmOTP user, String ploID, Double balance, int status, int parkingStatusID);
    void createCustomer(RequestConfirmOTP  user, String customerID, Double wallet_balance, int status);
    void updateStatusCustomerPhoneNumber(int status,String customerID);
    void updateStatusPLOPhoneNumber(int status,String ploID);
    void updateNewPasswordPLO(RequestChangePassword password,String ID);
    void updateNewPasswordCustomer(RequestChangePassword password,String ID);
}
