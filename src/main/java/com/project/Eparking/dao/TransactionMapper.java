package com.project.Eparking.dao;

import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.TransactionMethod;
import com.project.Eparking.domain.request.RequestGetTransactionPLOByID;
import com.project.Eparking.domain.request.RequestPLOTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    void insertTransactionPLO(RequestPLOTransaction ploTransaction);
    PLOTransaction getTransactionPLOByID(RequestGetTransactionPLOByID transactionPLOByID);
    TransactionMethod getTransactionMethodByHistoryID(String historyID);
    PLOTransaction getTransactionByUUID(String UUID);
    void insertTransactionMethod(TransactionMethod transactionMethod);
}
