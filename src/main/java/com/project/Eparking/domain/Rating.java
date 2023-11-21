package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private int ratingID;
    private int star;
    private String content;
    private String customerID;
    private String ploID;
    private int reservationID;
    private Timestamp feedbackDate;
}
