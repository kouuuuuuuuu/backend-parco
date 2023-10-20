package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegisterParking {
    private String parkingName;
    private List<String> images;
    private double length;
    private double width;
    private int slot;
    private String address;
    private String description;
    private String UUID;
}
