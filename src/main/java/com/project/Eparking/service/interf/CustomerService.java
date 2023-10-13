package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    List<CustomerDTO> getListCustomer(int pageNum, int pageSize);
}
