package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWalletScreen {
    private double wallet_balance;
    List<ResponseHistoryBalanceCustomer> historyBalanceCustomerList;
}
