package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseGuest {
    private int reservationID;
    private String type;
    private String customerID;
    private String ploID;
    private int statusID;
    private String statusName;
    private String licensePlate;
    private String checkOut;
    private String checkIn;
    private String startTime;
    private String endTime;
    private String image;
    private int methodID;
    private String methodName;
    private double priceMethod;
    private double total;
}
