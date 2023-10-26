package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFindParkingList {
    private String parkingName;
    private int currentSlot;
    private String address;
    private Double distance;
    private Double price;
    private Timestamp currentTime;
    private String methodName;
}
