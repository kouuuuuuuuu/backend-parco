package com.project.Eparking.service.interf;


import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;

import java.util.List;

public interface ParkingLotOwnerService {

    List<ListPloDTO> getListPloByStatus(int status, int pageNum, int pageSize);

    List<ListPloDTO> getListPloByKeywords(String keyword, int pageNum, int pageSize);
    ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception;

}
