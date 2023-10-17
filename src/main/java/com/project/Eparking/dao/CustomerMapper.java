package com.project.Eparking.dao;

import com.project.Eparking.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer getCustomerById(String customerID);

    List<Customer> getListCustomer(int pageNum, int pageSize);

    Integer countRecords();
}
