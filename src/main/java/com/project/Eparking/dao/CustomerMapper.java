package com.project.Eparking.dao;

import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.Response4week;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface CustomerMapper {
    Customer getCustomerById(String customerID);

    List<Customer> getListCustomer(int pageNum, int pageSize);

    Integer countRecords(String keywords);

    List<Customer> getListCustomerByName(String name, int pageNum, int pageSize);
    void updateCustomerProfile(RequestCustomerUpdateProfile profile,String customerID);
    void updatePasswordCustomer(String password,String customerID);
    void updateBalance(String customerID,Double balance);
    Response4week countRecordsByWeekCustomer(Date inputDate);

//    Customer getCustomerBalance(String customerID);

    Customer getCustomerBalance(String customerID);
}
