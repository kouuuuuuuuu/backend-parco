package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseTop5Revenue {
    private String ploID;
    private String fullName;
    private String parkingName;
    private Double revenue;
}
