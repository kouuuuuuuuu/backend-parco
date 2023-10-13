package com.project.Eparking.service.interf;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestParkingSetting;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.request.RequestUpdateProfilePLO;
import com.project.Eparking.domain.response.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ParkingService {

    void addParking(RequestRegisterParking registerParking);

    ResponseEntity<?> getParkingStatusOrList(String ploID);

    void updateParkingStatusID(String ploID, int parkingStatusID);

    List<ResponseShowVehicleInParking> showListVehicleInParking(int parkingStatusID);

    ParkingInformation getParkingInformation();

    ParkingInformation updateParkingInformation(RequestUpdateProfilePLO plo);

    ResponseReservationDetail getReservationDetailByPLOID(int reservationID);

    ResponseParkingSettingWithID getParkingSettingByPLOID();
    void settingParking(List<RequestParkingSetting> settings);
    List<ReservationMethod> getAllReservationMethod();
}
