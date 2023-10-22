package com.project.Eparking.dao;

import com.project.Eparking.domain.request.RequestCustomerTransactionMapper;
import com.project.Eparking.domain.response.ResponseHistoryBalanceCustomer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomerTransactionMapper {
    void insertCustomerTransaction(RequestCustomerTransactionMapper transactionMapper);
    List<ResponseHistoryBalanceCustomer> getListTransactionCustomer(String customerID);
}
