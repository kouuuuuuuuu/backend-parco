package com.project.Eparking.dao;

import com.project.Eparking.domain.Motorbike;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MotorbikeMapper {
    List<Motorbike> getListLicensePlateByCustomerID(String customerID);

    Motorbike getLicensePlateById(int motorbikeID);

    int deleteLicensePlate(int motorbikeID, String customerID);

    void createLicensePlate(Motorbike motorbike, String customerID);

    Motorbike getLicensePlateByLicensePlate(int motorbikeID, String customerID);

    List<Motorbike> getListLicensePlate();

    void updateLicensesPlateStatusById(int motorbikeID, String customerID);

    List<Motorbike> getAllLicensePlateByCustomerId(String id);

    Motorbike getLicensePlateByLicensePlateString(String licensePlate);
}
