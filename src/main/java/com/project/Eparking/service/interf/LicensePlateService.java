package com.project.Eparking.service.interf;

import com.project.Eparking.domain.LicensePlate;

import java.util.List;

public interface LicensePlateService {
    List<LicensePlate> getListLicensePlateByCustomerID(String customerID);
}
