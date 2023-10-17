package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestPLOTransaction {
    private String method;
    private String ploID;
    private int status;
    private double depositAmount;
    private Timestamp transactionDate;
    private Timestamp transactionResultDate;
}
