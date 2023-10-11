package com.project.Eparking.service.interf;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.request.RequestUpdateProfilePLO;
import com.project.Eparking.domain.response.ResponseParkingStatus;
import com.project.Eparking.domain.response.ResponseRegisterParking;
import com.project.Eparking.domain.response.ResponseShowVehicleInParking;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ParkingService {

    void addParking(RequestRegisterParking registerParking);

    ResponseEntity<?> getParkingStatusOrList(String ploID);

    void updateParkingStatusID(String ploID, int parkingStatusID);

    List<ResponseShowVehicleInParking> showListVehicleInParking(String ploID, int parkingStatusID);
    ParkingInformation getParkingInformation();
    ParkingInformation updateParkingInformation(RequestUpdateProfilePLO plo);

}
