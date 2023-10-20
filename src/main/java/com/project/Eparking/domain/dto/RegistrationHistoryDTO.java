package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationHistoryDTO extends ListPloDTO{
    private String browseContract;
    private String registerContract;
    public RegistrationHistoryDTO(String ploID, String fullName, String phoneNumber, String address, String parkingName) {
        super(ploID, fullName, phoneNumber, address, parkingName);
    }
}
