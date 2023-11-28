package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFindLicensePlateDTO {
    private List<FindLicensePlateDTO>  reservationHistory;
    private String customerName;
    private String phoneNumber;
    private int totalBooking;
}
