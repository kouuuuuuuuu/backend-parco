package com.project.Eparking.dao;

import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.TransactionMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMethodMapper {
    List<TransactionMethod> getListTransactionMethodByHistoryId(int historyID);

}
