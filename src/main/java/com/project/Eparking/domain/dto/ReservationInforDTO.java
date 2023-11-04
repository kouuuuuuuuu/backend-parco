package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInforDTO {
    private String customerName;
    private String methodName;
    private int status;
    private String statusName;
    private String checkIn;
    private String checkOut;
    private String startTime;
    private String endTime;
    private String licensePlate;
    private int reservationID;
}
