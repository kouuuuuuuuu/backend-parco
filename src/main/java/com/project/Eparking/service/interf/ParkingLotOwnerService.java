package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;


public interface ParkingLotOwnerService {

    ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception;
}
