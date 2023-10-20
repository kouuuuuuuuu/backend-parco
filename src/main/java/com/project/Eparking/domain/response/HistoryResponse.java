package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponse {
    private int historyID;
    private Double depositAmount;
    private Timestamp transactionDate;
    private Timestamp transactionResultDate;
}
