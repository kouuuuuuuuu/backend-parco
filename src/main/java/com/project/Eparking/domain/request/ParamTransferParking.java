package com.project.Eparking.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamTransferParking {
    private String phoneNumberTransfer;
    private String ploID;
    private String ploIDTransfer;
}
