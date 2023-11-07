package com.project.Eparking.domain;

import com.google.firebase.database.annotations.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Motorbike {
    private int motorbikeID;
    private String customerID;
    private String licensePlate;
    private boolean isDelete;
    private String motorbikeName;
    private String motorbikeColor;
}
