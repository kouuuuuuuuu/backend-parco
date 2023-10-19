package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseParkingList {
    private int parkingStatusID;
    private String statusName;
    private int totalVehicle;
    private List<ParkingComing> totalComing;
    private Double totalPriceToDay;
}
