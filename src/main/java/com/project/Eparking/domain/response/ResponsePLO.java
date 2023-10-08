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
    private Double balance;
    private String phoneNumber;
    private String fullName;
    private String email;
    private int status;
    private String identify;
    private String parkingName;
    private String Description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longtitude;
    private int parkingStatusID;
    private int slot;
    private String role;
    private Double length;
    private Double width;
    private Time waitingTime;
    private Time cancelBookingTime;
    private String contractLink;
    private Timestamp registerContract;
    private Timestamp browseContract;
    private Timestamp contractDuration;
}
