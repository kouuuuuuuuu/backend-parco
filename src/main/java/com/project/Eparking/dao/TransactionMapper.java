package com.project.Eparking.dao;

import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.TransactionMethod;
import com.project.Eparking.domain.request.RequestGetTransactionPLOByID;
import com.project.Eparking.domain.request.RequestPLOTransaction;
import com.project.Eparking.domain.response.HistoryResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
  
    void insertTransactionPLO(RequestPLOTransaction ploTransaction);
  
    PLOTransaction getTransactionPLOByID(RequestGetTransactionPLOByID transactionPLOByID);
  
    TransactionMethod getTransactionMethodByHistoryID(String historyID);
  
    PLOTransaction getTransactionByUUID(String UUID);
  
    void insertTransactionMethod(TransactionMethod transactionMethod);
  
    List<HistoryResponse> historyTransactionByPLOandStatus(String ploID, int status);
  
    List<PLOTransaction> getPagePloTransactionByStatus(List<Integer> status, int pageNum, int pageSize);

    Integer countRecords(List<Integer> status, String keywords);

    PLOTransaction getPloTransactionByHistoryId(int transactionId);

    void updatePloTransactionStatusByHistoryId(int transactionId, int status);
  
}
