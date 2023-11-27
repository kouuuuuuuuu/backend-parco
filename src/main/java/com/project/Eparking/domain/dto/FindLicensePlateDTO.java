package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindLicensePlateDTO {
    private String ploID;
    private String ploName;
    private String checkIn;
    private String checkOut;
    private MotorbikeDTO motorbikeDTOS;
}
