package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCustomerTransactionMapper {
    private String customerID;
    private Double depositAmount;
    private Timestamp rechargeTime;
    private String bankCode;
    private String vnPay_ref;
}
