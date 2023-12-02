package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageGuest {
    private int imageguestID;
    private int reservationID;
    private String imageLink;
}
