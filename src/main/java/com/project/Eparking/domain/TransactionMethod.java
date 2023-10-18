package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionMethod {
    private String transactionMethodID;
    private int historyID;
    private String bankCode;
    private String bankName;
    private String bankNumber;
}
