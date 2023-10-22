package com.project.Eparking.dao;

import com.project.Eparking.domain.request.RequestCustomerTransactionMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerTransactionMapper {
    void insertCustomerTransaction(RequestCustomerTransactionMapper transactionMapper);
}
