package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
import com.project.Eparking.domain.dto.PloRegistrationDTO;
import com.project.Eparking.domain.dto.UpdatePloStatusDTO;

import java.util.List;

public interface ParkingLotOwnerService {

    List<ListPloDTO> getListPloByKeywords(String keyword, int pageNum, int pageSize);
    ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception;
    List<ListPloDTO> getPloByParkingStatus(int status, int pageNum, int pageSize);
    PloRegistrationDTO getPloRegistrationByPloId(String ploId);
    List<ListPloDTO> getListRegistrationByParkingStatus(int status, int pageNum, int pageSize);
    boolean updatePloStatusById(UpdatePloStatusDTO updatePloStatusDTO);
}
