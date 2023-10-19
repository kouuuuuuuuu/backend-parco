package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PloWithdrawalDTO {

    private int transactionID;
    private String ploID;
    private String statusName;
    private String address;
     private String parkingName;
     private String fullName;
     private double depositAmount;
     private double balance;
     private String transactionDate;
     private String transactionResultDate;
     private String phoneNumber;

     private List<WithdrawalTransactionMethodDTO> transactionMethod;
}
