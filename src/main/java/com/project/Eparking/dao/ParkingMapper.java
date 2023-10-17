package com.project.Eparking.dao;


import com.project.Eparking.domain.request.*;

import com.project.Eparking.domain.request.RequestImage;
import com.project.Eparking.domain.request.RequestParking;
import com.project.Eparking.domain.request.RequestParkingSettingMapper;
import com.project.Eparking.domain.request.RequestUpdateProfilePLO;

import com.project.Eparking.domain.response.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ParkingMapper {

    void registerParking(RequestParking requestParking);

    void addImage(RequestImage requestImage);

    List<Map<String, Object>> findParkingInformationByPLOID(String ploID);

    ResponseParkingStatus getParkingStatus(String ploID);

    void updateParkingStatusID(String ploID, int parkingStatusID);

    List<ParkingComing> getListParkingOngoing();

    List<ResponseShowVehicleInParking> showListVehicleInParking(String ploID, int statusID);

    void updateParkingProfile(RequestUpdateProfilePLO plo,String ploID);

    ResponseReservationDetail getReservationDetailByReservationID(int ID);
  
    void updateParkingOwner(ParamTransferParking transferParking);

    List<ResponseParkingSetting> getParkingSettingByPLOID(String ploID);
  
    void deleteParkingSetting(String ploID);
  
    void batchInsertSettingMethod(List<RequestParkingSettingMapper> settings);

    List<ResponseShowVehicleInParking> showListVehicleInParkingByParkingID(String ploID, int statusID);

}
