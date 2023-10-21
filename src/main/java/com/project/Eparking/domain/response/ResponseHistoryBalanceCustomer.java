package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHistoryBalanceCustomer {
    private int transactionID;
    private double depositAmount;
    private Timestamp rechargeTime;
    private String bankCode;

}
