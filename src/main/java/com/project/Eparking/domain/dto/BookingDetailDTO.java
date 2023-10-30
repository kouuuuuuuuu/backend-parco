package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetailDTO {
    private String ploID;
    private String parkingName;
    private String address;
    private double morningFee;
    private double eveningFee;
    private double overnightFee;
    private List<ReservationBookingMethodDTO> reservationMethod;
    private List<LicensePlateDTO> customerLicensePlate;
    private String waitingTime;
}
