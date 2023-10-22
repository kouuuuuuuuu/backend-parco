package com.project.Eparking.service.impl;

import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.ParkingLotOwnerMapper;
import com.project.Eparking.domain.dto.CustAndPloDTO;
import com.project.Eparking.service.interf.CustAndPloService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@RequiredArgsConstructor
@Slf4j
@Service
public class CustAndPloServiceImpl implements CustAndPloService {

    private final CustomerMapper customerMapper;

    private final ParkingLotOwnerMapper parkingLotOwnerMapper;

    @Override
    public CustAndPloDTO getTotalCustAndPlo() {
        long totalCustomerList = customerMapper.countRecords("");

        long totalPloList = parkingLotOwnerMapper.countRecords(new ArrayList<>(), "");
        CustAndPloDTO custAndPloDTO = new CustAndPloDTO(totalCustomerList, totalPloList);

        return custAndPloDTO;
    }
}
