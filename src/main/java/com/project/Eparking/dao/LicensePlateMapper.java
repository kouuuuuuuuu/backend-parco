package com.project.Eparking.dao;

import com.project.Eparking.domain.LicensePlate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface LicensePlateMapper {
    List<LicensePlate> getListLicensePlateByCustomerID(String customerID);

    LicensePlate getLicensePlateById(int licensePlateID);
  
    int deleteLicensePlate(String licensePlateID, String customerID);

    void createLicensePlate(String licensePlate, String customerID);

    LicensePlate getLicensePlateByLicensePlate(String licensePlate, String customerID);

    List<LicensePlate> getListLicensePlate();

    void updateLicensesPlateStatusById(int licensePlateID, String customerID);

    List<LicensePlate> getAllLicensePlateByCustomerId(String id);

    LicensePlate getLicensePlateByLicensePlateString(String licensePlate);
}
