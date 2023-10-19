package com.project.Eparking.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class ParkingLotOwnerDTO {
    private String ploID;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String parkingName;
    private String email;
    private Timestamp contractDuration;
    private String contractLink;
    private double length;
    private double width;
    private int slot;
    private int currentSlot;
    private List<ImageDTO> images;
    private int star;
    private String latitude;
    private String longtitude;
    private int status;
    private double morningFee;
    private double eveningFee;
    private double overNightFee;
    private int parkingStatusID;
}
