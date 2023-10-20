package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTop5Parking {
    private String ploID;
    private String parkingName;
    private String fullName;
    private int total;
}
