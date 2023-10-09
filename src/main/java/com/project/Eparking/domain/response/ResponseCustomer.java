package com.project.Eparking.domain.response;

import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.LicensePlate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCustomer {
    private String customerID;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String email;
}
