package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingInformation {
    private String ploID;
    private String parkingName;
    private String description;
    private String address;
    private int slot;
    private Double length;
    private Double width;
    private List<Image> image;

}
