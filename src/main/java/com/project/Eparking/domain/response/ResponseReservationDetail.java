package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseReservationDetail {
    private String licensePlate;

    private Double price;

    private String methodName;

    private String fullName;
    private String phoneNumber;
    private String statusName;

    private Timestamp startTime;

    private Timestamp endTime;

    private Timestamp checkIn;

    private Timestamp checkOut;
    private double totalPrice;
    private String image;
}
