package com.project.Eparking.dao;

import com.project.Eparking.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper {
    Customer getCustomerById(String customerID);
}
