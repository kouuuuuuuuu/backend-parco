package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {
    private int reservationID;
    private String parkingName;
    private String address;
    private String methodName;
    private String checkIn;
    private String checkOut;
}
