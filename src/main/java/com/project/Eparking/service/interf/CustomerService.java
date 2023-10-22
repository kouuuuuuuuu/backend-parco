package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.request.RequestChangePassword;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.ResponseCustomer;

import java.util.List;

public interface CustomerService {
    Page<CustomerDTO> getListCustomer(int pageNum, int pageSize);

    Page<CustomerDTO> getListCustomerByName(String name, int pageNum, int pageSize);
    ResponseCustomer getResponseCustomerByCustomerID();
    String updateCustomerProfile(RequestCustomerUpdateProfile profile);
    List<String> updatePassswordCustomer(RequestChangePasswordUser customer);
}
