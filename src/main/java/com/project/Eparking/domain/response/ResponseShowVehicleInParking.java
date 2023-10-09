package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseShowVehicleInParking {

    private String customerID;
    private String fullName;
    private double price;
    private String phoneNumber;
    private String licensePlate;
    private Instant startTime;
    private Instant endTime;
    private String methodName;
    private String statusName;


}
