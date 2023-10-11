package com.project.Eparking.domain.dto;

import java.sql.Timestamp;
import java.util.List;

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
    private String longtitule;
    private int status;
    private double morningFee;
    private double eveningFee;
    private double overNightFee;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getContractDuration() {
        return contractDuration;
    }

    public void setContractDuration(Timestamp contractDuration) {
        this.contractDuration = contractDuration;
    }

    public String getContractLink() {
        return contractLink;
    }

    public void setContractLink(String contractLink) {
        this.contractLink = contractLink;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public List<ImageDTO> getImages() {
        return images;
    }

    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitule() {
        return longtitule;
    }

    public void setLongtitule(String longtitule) {
        this.longtitule = longtitule;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getMorningFee() {
        return morningFee;
    }

    public void setMorningFee(double morningFee) {
        this.morningFee = morningFee;
    }

    public double getEveningFee() {
        return eveningFee;
    }

    public void setEveningFee(double eveningFee) {
        this.eveningFee = eveningFee;
    }

    public double getOverNightFee() {
        return overNightFee;
    }

    public void setOverNightFee(double overNightFee) {
        this.overNightFee = overNightFee;
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(int currentSlot) {
        this.currentSlot = currentSlot;
    }
}
