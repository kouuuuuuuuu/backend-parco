package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PLOTransaction {
    private int historyID;
    private String ploID;
    private int status;
    private double depositAmount;
    private Timestamp transactionDate;
    private Timestamp transactionResultDate;
    private String vnPay_ref;
    private String UUID;
}
