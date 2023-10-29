package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFindParkingList {
    private String ploID;
    private String parkingName;
    private String address;
    private Double distance;
    private Double price;
    private Timestamp currentTime;
    private String methodName;
    private int slot;
    private List<ResponseMethod> listMethod;
}
