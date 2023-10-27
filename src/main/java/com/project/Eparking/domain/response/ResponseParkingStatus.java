package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseParkingStatus {
    private String ploID;
    private int parkingStatusID;
    private String statusName;
//    private Double totalPriceToDay;
}
