package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRevenuePLO {
    private String ploID;
    private Double balance;
    private int TotalVehicle;
    private int TotalVehicleMethodDay;
    private int TotalVehicleMethodNight;
    private int TotalVehicleMethodOvernight;
    private List<HistoryResponse> history;
}
