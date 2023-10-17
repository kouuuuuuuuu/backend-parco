package com.project.Eparking.domain.request;

import com.project.Eparking.domain.TimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateProfilePLO {
    private String parkingName;
    private String description;
    private int slot;
    private Time waitingTime;
    private Time cancelBookingTime;
    private List<String> image;
}
