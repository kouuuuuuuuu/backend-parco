package com.project.Eparking.domain.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePloStatusDTO {
    private String ploId;
    private int newStatus;
    private String contractLink;
    private int contractDuration;
//    private BigDecimal latitude;
//    private BigDecimal longtitude;
}
