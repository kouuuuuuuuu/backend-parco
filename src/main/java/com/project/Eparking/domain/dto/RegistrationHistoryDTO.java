package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationHistoryDTO extends ListPloRegistrationDTO{
    private String browseContract;
    public RegistrationHistoryDTO(String ploID, String fullName, String phoneNumber, String address, String parkingName) {
        super(ploID, fullName, phoneNumber, address, parkingName);
    }
}
