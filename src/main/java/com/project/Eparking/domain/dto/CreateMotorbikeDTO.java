package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMotorbikeDTO {
    private String licensePlate;
    private String motorbikeName;
    private String motorbikeColor;
}
