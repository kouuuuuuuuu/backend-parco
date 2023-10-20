package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPLOTransactionWithdrawa {
    private String ploID;
    private int status;
    private Double depositAmount;
    private Timestamp transactionDate;

}
