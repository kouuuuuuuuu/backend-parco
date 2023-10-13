package com.project.Eparking.domain.dto;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class ListPloDTO{

    private String ploID;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String parkingName;

    public ListPloDTO(String ploID, String fullName, String phoneNumber, String address, String parkingName) {
        this.ploID = ploID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.parkingName = parkingName;
    }

    public ListPloDTO() {
    }
}
