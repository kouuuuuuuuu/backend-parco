package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingComing {

    private int reservationID;
    private String licensePlate;
    private String fullName;
    private String methodName;

}
