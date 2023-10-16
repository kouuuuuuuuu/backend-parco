package com.project.Eparking.dao;

import com.project.Eparking.domain.request.RequestPLOTransaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransactionMapper {
    void insertTransactionPLO(RequestPLOTransaction ploTransaction);
}
