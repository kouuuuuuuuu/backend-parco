package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMethodByTime {
    private int methodID;
    private String methodName;
    private Double price;
    private boolean isSpecial;
}
