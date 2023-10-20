package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestGetSumPLO {
    private Date startTime;
    private Date startTime2nd;
}
