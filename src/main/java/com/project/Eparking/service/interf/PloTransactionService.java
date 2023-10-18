package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.PloWithdrawalDTO;
import com.project.Eparking.domain.response.Page;

public interface PloTransactionService {
    Page<PloWithdrawalDTO> getListWithdrawalByStatus(int status, int pageNum, int pageSize);

    boolean updateWithdrawalStatus(int transactionId, int status);
}
