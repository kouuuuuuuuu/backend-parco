package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
import com.project.Eparking.domain.dto.PloRegistrationDTO;
import com.project.Eparking.domain.dto.UpdatePloStatusDTO;
import com.project.Eparking.domain.response.Page;

import java.util.List;

public interface ParkingLotOwnerService {

    Page<ListPloDTO> getListPloByKeywords(String keyword, int parkingStatus, int pageNum, int pageSize);
    ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception;
    PloRegistrationDTO getPloRegistrationByPloId(String ploId);
    Page<ListPloDTO> getListRegistrationByParkingStatus(int status, int pageNum, int pageSize);
    boolean updatePloStatusById(UpdatePloStatusDTO updatePloStatusDTO);
    Page<ListPloDTO> getPloByParkingStatus(int status, int pageNum, int pageSize);
}
