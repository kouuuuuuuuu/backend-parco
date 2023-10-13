package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseParkingSetting {
    private int methodID;
    private String methodName;
    private Time startTime;
    private Time endTime;
    private Double price;
}
