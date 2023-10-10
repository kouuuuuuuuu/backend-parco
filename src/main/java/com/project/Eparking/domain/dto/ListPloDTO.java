package com.project.Eparking.domain.dto;

public class ListPloDTO{

    private String ploID;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String parkingName;

    public String getPloID() {
        return ploID;
    }

    public void setPloID(String ploID) {
        this.ploID = ploID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getParkingName() {
        return parkingName;
    }

    public void setParkingName(String parkingName) {
        this.parkingName = parkingName;
    }
}
