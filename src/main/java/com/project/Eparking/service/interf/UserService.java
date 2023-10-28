package com.project.Eparking.service.interf;

import com.project.Eparking.domain.*;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.*;

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
    ResponsePLOProfile updatePLOprofile(RequestPLOupdateProfile profile);
//    List<String> changePasswordUser(RequestChangePasswordUser password);
    double getBalancePlO();
    List<ResponseNotifications> getListNotificationByID();
    ResponseProfile profileUser();
    String updateProfile(RequestUpdateProfile requestUpdateProfile);
    List<String> changePasswordUser(RequestChangePasswordUser changePasswordUser);
}
