package com.project.Eparking.controller;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestParkingSetting;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingService parkingService;
    private final UserService userService;

    @PostMapping("/registerParking")
    public ResponseEntity<MessageResponse> registerParking(@RequestBody RequestRegisterParking registerParking,
                                                  HttpServletResponse response,
                                                  HttpServletRequest request) {
        try {
            parkingService.addParking(registerParking);
            return ResponseEntity.ok(new MessageResponse("Register parking successfully"));
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Register parking failure"));
        }
    }

    @GetMapping("/getParkingStatusID")
    public ResponseEntity<?> getParkingStatusID(@RequestParam String ploID,
                                                HttpServletResponse response,
                                                HttpServletRequest request) {
        return parkingService.getParkingStatusOrList(ploID);
    }

    @PutMapping("/updateParkingStatusID")
    public ResponseEntity<MessageResponse> updateParkingStatusID(
            @RequestParam String ploID,
            @RequestParam int parkingStatusID,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            parkingService.updateParkingStatusID(ploID, parkingStatusID);
            return ResponseEntity.ok(new MessageResponse("Update status successfully"));
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Register parking failure"));
        }
    }

    @GetMapping("/showListVehicleInParking")
    public ResponseEntity<List<ResponseShowVehicleInParking>> showListVehicleInParkingByStatusID(
            @RequestParam int statusID,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            List<ResponseShowVehicleInParking> responseShowVehicleInParking = parkingService.showListVehicleInParking(statusID);
            return ResponseEntity.ok(responseShowVehicleInParking);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
    @GetMapping("/getReservationDetail")
    public ResponseEntity<ResponseReservationDetail> getReservationDetail(@RequestParam int reservationID){
        try{
            return ResponseEntity.ok(parkingService.getReservationDetailByPLOID(reservationID));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @GetMapping("/getParkingSetting")
    public ResponseEntity<ResponseParkingSettingWithID> getParkingSettingPLO(){
        try{
            return ResponseEntity.ok(parkingService.getParkingSettingByPLOID());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PostMapping("/updateParkingSetting")
    public ResponseEntity<String> updateParkingSetting(@RequestBody List<RequestParkingSetting> settings){
        try{
            parkingService.settingParking(settings);
            return ResponseEntity.ok("Update setting successfully");
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getAllReservationMethod")
    public ResponseEntity<List<ReservationMethod>> getAllReservationMethod(){
        try {
            return ResponseEntity.ok(parkingService.getAllReservationMethod());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/showListVehicleInParkingByParkingStatus")
    public ResponseEntity<List<ResponseShowVehicleInParking>> showListVehicleInParkingByParkingStatus(
            @RequestParam int parkingStatus,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            List<ResponseShowVehicleInParking> responseShowVehicleInParking = parkingService.showListVehicleInParkingByParkingID(parkingStatus);
            return ResponseEntity.ok(responseShowVehicleInParking);
        } catch (ApiRequestException e) {
            throw e;
        }
    }
}
