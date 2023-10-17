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
public class RequestUpdateProfilePLOTime {
    private String parkingName;
    private String description;
    private int slot;
    private TimeFormat waitingTime;
    private TimeFormat cancelBookingTime;
    private List<String> image;
}
