package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PloDetailForCustomerDTO {
    private String parkingName;
    private String address;
    private double morningFee;
    private double eveningFee;
    private double overnightFee;
    private int star;
    private int currentSlot;
    private List<ImageDTO> images;
}
