package com.project.Eparking.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

public class ListPloRegistrationDTO extends ListPloDTO{

    private String registerContract;

    public ListPloRegistrationDTO(String ploID, String fullName, String phoneNumber, String address, String parkingName) {
        super(ploID, fullName, phoneNumber, address, parkingName);
    }

    public String getRegisterContract() {
        return registerContract;
    }

    public void setRegisterContract(String registerContract) {
        this.registerContract = registerContract;
    }

    @JsonIgnore
    @Override
    public String getAddress() {
        return super.getAddress();
    }
}
