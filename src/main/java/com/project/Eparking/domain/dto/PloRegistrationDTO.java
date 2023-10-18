package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
public class PloRegistrationDTO {
    private String ploID;
    private Double balance;
    private String phoneNumber;
    private String fullName;
    private String password;
    private String email;
    private int status;
    private String identify;
    private String parkingName;
    private String Description;
    private String address;
    private String latitude;
    private String longtitude;
    private int parkingStatusID;
    private int slot;
    private int currentSlot;
    private String role;
    private Double length;
    private Double width;
    private Time waitingTime;
    private Time cancelBookingTime;
    private String contractLink;
    private String registerContract;
    private String browseContract;
    private String contractDuration;
    private int star;
}
