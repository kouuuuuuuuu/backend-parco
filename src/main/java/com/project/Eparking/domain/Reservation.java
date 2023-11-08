package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {
    private int reservationID;
    private String customerID;
    private String ploID;
    private int statusID;
    private int licensePlateID;
    private Timestamp startTime;
    private Timestamp endTime;
    private double price;
    private Timestamp checkOut;
    private Timestamp checkIn;
    private int methodID;
    private int isRating;
}
