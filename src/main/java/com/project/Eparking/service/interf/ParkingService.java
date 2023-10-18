package com.project.Eparking.service.interf;

import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.ParkingInformation;

import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.request.*;

import com.project.Eparking.domain.ReservationMethod;

import com.project.Eparking.domain.response.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ParkingService {

    String addParking(RequestRegisterParking registerParking);

    ResponseEntity<?> getParkingStatusOrList();

    void updateParkingStatusID(int parkingStatusID);

    List<ResponseShowVehicleInParking> showListVehicleInParking(int parkingStatusID);

    ParkingInformation getParkingInformation();

    ParkingInformation updateParkingInformation(RequestUpdateProfilePLOTime plo);

    ResponseReservationDetail getReservationDetailByPLOID(int reservationID);
    String checkPLOTransfer(RequestTransferParking requestTransferParking);


    String checkOTPcodeTransferParking(RequestCheckOTPTransferParking transferParking);

    ResponseParkingSettingWithID getParkingSettingByPLOID();
    void settingParking(List<RequestParkingSetting> settings);
    List<ReservationMethod> getAllReservationMethod();

    List<ResponseShowVehicleInParking> showListVehicleInParkingByParkingID(int parkingStatusID);

    PLOTransaction checkPLOPayment();
    Map<String, Object> paymentParkingRegister(HttpServletRequest req);
}
