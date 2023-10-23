package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParking {
    private String ploID;
    private String parkingName;
    private List<String> images;
    private double length;
    private double width;
    private int slot;
    private String address;
    private String description;
    private int parkingStatusID;
    private int currentSlot;
    private Timestamp registerContract;
}
