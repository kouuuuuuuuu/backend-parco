package com.project.Eparking.service.impl;

import com.project.Eparking.dao.LicensePlateMapper;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.LicensePlateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class LicensePlateImpl implements LicensePlateService {
    private final LicensePlateMapper licensePlateMapper;

    @Override
    public List<LicensePlate> getListLicensePlateByCustomerID(String customerID) {
        try {
            return licensePlateMapper.getListLicensePlateByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get list license plate by customerID" +e.getMessage());
        }
    }
}
