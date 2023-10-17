package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.response.Page;

import java.util.List;

public interface CustomerService {
    Page<CustomerDTO> getListCustomer(int pageNum, int pageSize);
}
