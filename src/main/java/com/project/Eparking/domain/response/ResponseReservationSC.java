package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseReservationSC {
    private int reservationID;
    private String ploID;
    private int statusID;
    private String statusName;
    private String parkingName;
    private String address;
    private Double longitude;
    private Double latitude;
    private Double price;
    private String methodName;
    private String licensePlate;
    private Timestamp startTime;
    private Timestamp endTime;
    private Time waitingTime;
    private Time cancelBookingTime;
}
