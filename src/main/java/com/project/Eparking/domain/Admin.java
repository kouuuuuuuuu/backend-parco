package com.project.Eparking.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    private String adminID;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String password;
    private String email;
    private int status;
}