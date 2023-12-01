package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseShowVehicleInParking {
    private String reservationID;
    private String customerID;
    private String fullName;
    private double price;
    private String phoneNumber;
    private String licensePlate;
    private Timestamp startTime;
    private Timestamp endTime;
    private String methodName;
    private String statusName;
    private double totalPrice;
}
