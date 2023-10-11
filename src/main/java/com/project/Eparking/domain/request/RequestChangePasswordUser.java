package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestChangePasswordUser {
    private String currentPassword;
    private String newPassword;
    private String reNewPassword;
}
