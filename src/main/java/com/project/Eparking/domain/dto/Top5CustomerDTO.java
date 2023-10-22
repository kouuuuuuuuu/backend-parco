package com.project.Eparking.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Top5CustomerDTO {
    private String fullName;
    private String phoneNumber;
    private int total;
}
