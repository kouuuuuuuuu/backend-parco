package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.PloWithdrawalDTO;
import com.project.Eparking.domain.response.Page;

public interface PloTransactionService {
    boolean updateWithdrawalStatus(int transactionId, int status);

    Page<PloWithdrawalDTO> searchWithdrawalByKeyword(int Status,String keywords, int pageNum, int pageSize);
}
