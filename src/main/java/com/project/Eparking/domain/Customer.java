package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String customerID;
    private double walletBalance;
    private String identify;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String password;
    private String email;
    private int status;
    private Timestamp registrationDate;
}
