package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestUpdateProfilePLO {
    private String parkingName;
    private String description;
    private List<String> image;
}
