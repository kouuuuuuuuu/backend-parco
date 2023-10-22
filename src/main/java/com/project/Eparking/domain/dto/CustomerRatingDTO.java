package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRatingDTO {
    private String customerID;
    private String customerName;
    private int rating;
    private String content;
    private String feedbackDate;
}
