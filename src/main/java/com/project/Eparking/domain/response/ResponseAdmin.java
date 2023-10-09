package com.project.Eparking.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAdmin {
    private String adminID;
    private String image;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String email;
}
