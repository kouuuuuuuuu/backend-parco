package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInforDTO {
    private int reservationID;
    private String customerID;
    private String customerName;
    private int methodID;
    private String methodName;
    private int statusID;
    private String statusName;
    private String checkIn;
    private String checkOut;
    private String startTime;
    private String endTime;
    private String licensePlate;
    private String motorbikeName;
    private String motorbikeColor;
    private double total;
    private String image;
}
