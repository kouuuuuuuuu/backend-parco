package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePLO {
    private String ploID;
    private String fullName;
    private String email;
    private String address;
    private int parkingStatusID;
    private String statusName;
    private String role;
}
