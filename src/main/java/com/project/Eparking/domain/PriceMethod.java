package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceMethod {
    private int priceReservationID;
    private int reservationID;
    private double total;
    private double method1;
    private double method2;
    private double method3;
}
