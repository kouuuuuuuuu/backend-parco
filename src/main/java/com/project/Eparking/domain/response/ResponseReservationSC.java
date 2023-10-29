package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
