package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response4week;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponseWalletScreen;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CustomerService {
    Page<CustomerDTO> getListCustomer(int pageNum, int pageSize);

    Page<CustomerDTO> getListCustomerByName(String name, int pageNum, int pageSize);
    ResponseCustomer getResponseCustomerByCustomerID();
    String updateCustomerProfile(RequestCustomerUpdateProfile profile);
    List<String> updatePassswordCustomer(RequestChangePasswordUser customer);
    ResponseEntity<?> createPaymentCustomer(HttpServletRequest req,RequestCustomerTransaction transaction);
    ResponseWalletScreen responseWalletScreen();
    Response4week countRecordsByWeekCustomer(RequestMonthANDYear requestMonthANDYear);
}
