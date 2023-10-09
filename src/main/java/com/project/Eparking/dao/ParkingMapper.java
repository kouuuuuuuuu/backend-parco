package com.project.Eparking.dao;

import com.project.Eparking.domain.request.RequestImage;
import com.project.Eparking.domain.request.RequestParking;
import com.project.Eparking.domain.response.ParkingComing;
import com.project.Eparking.domain.response.ResponseParkingStatus;
import com.project.Eparking.domain.response.ResponseShowVehicleInParking;
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

}
