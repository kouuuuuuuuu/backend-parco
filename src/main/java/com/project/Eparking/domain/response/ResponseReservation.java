package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseReservation {
    private int reservationID;
    private String customerID;
    private String ploID;
    private int status;
    private String licensePlate;
    private Timestamp startTime;
    private Timestamp endTime;
    private double price;
    private Timestamp checkOut;
    private Timestamp checkIn;
    private String methodName;
}
