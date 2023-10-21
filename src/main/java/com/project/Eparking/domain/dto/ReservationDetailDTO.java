package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetailDTO {
    private double fee;
    private String statusName;
    private String parkingName;
    private String methodName;
    private String licensePlate;
    private String address;
    private String startTime;
    private String endTime;
    private String checkIn;
    private String checkOut;
}
