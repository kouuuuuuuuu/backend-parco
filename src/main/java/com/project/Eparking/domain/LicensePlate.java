package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicensePlate {
    private int licensePlateID;
    private String customerID;
    private String licensePlate;
}
