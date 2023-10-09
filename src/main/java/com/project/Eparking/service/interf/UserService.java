package com.project.Eparking.service.interf;

import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.request.RequestConfirmOTP;
import com.project.Eparking.domain.request.RequestRegisterUser;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;

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
}
