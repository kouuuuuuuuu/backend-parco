package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.*;
import com.project.Eparking.domain.request.RequestMonthANDYear;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response4week;
import com.project.Eparking.domain.response.WeekData;

import java.util.List;

public interface ParkingLotOwnerService {

    Page<ListPloDTO> getListPloByKeywords(String keyword, int parkingStatus, int pageNum, int pageSize);
    ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception;
    PloRegistrationDTO getPloRegistrationByPloId(String ploId);
    Page<ListPloDTO> getListRegistrationByParkingStatus(int status, int pageNum, int pageSize, String keywords);
    boolean updatePloStatusById(UpdatePloStatusDTO updatePloStatusDTO);
    Page<ListPloDTO> getPloByParkingStatus(int status, int pageNum, int pageSize);
    Page<RegistrationHistoryDTO> getListRegistrationHistory(int status, int pageNum, int pageSize, String keywords);
    List<WeekData> chartPLO(RequestMonthANDYear requestMonthANDYear);
}
