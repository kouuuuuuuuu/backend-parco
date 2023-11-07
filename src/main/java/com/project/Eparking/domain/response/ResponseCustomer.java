package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
