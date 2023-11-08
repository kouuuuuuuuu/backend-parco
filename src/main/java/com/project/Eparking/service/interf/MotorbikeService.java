package com.project.Eparking.service.interf;

import com.project.Eparking.domain.Motorbike;
import com.project.Eparking.domain.dto.CreateMotorbikeDTO;
import com.project.Eparking.domain.dto.MotorbikeDTO;

import java.util.List;

public interface MotorbikeService {
    List<Motorbike> getListLicensePlateByCustomerID(String customerID);

    List<MotorbikeDTO> getListLicensePlate();

    boolean deleteLicensePlateByLicensePlateID(int licensePlateID);

    String addLicensePlate(CreateMotorbikeDTO motorbikeDTO);
}
