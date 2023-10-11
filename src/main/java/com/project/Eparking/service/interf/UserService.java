package com.project.Eparking.service.interf;

import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;
import com.project.Eparking.domain.response.ResponsePLOProfile;

import java.util.List;

public interface UserService {
    Admin getAdminByAdminID(String adminID);
    Customer getCustomerByCustomerID (String customerID);
    PLO getPLOByPLOID(String ploID);
    ResponseCustomer getResponseCustomerByCustomerID(String customerID);
    ResponsePLO getPLOResponseByPLOID(String ploID);
    ResponseAdmin getAdminResponseByAdminID(String adminID);
    String registerPLO(RequestRegisterUser user);
    String registerCustomer(RequestRegisterUser user);
    String registerConfirmOTPcode(RequestConfirmOTP requestConfirmOTP);
    String checkPhoneNumber(RequestForgotPassword requestForgotPassword);
    String forgotPasswordCheckOTP(RequestForgotPasswordOTPcode requestForgotPasswordOTPcode);
    String updatePasswordUser(RequestChangePassword password);
    ResponsePLOProfile getPLOProfileResponseByPLOID();
    ResponsePLOProfile updatePLOprofile(RequestPLOupdateProfile profile);
    List<String> changePasswordUser(RequestChangePasswordUser password);
}
