package com.project.Eparking.dao;

import com.project.Eparking.domain.LicensePlate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface LicensePlateMapper {
    List<LicensePlate> getListLicensePlateByCustomerID(String customerID);

    LicensePlate getLicensePlateById(int licensePlateID);
}
