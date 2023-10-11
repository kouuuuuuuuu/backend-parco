package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingMethod {
    private int parkingMethodID;
    private String ploID;
    private int methodID;
    private double price;
}
