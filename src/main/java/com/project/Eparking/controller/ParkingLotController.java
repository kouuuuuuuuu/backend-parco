package com.project.Eparking.controller;

import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestParkingSetting;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.PaymentService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/parking")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingService parkingService;
    private final UserService userService;
    private final PaymentService paymentService;

    @PostMapping("/registerParking")
    public ResponseEntity<?> registerParking(@RequestBody RequestRegisterParking registerParking,
                                                  HttpServletResponse response,
                                                  HttpServletRequest request, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(parkingService.addParking(registerParking,req));
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Register parking failure"));
        }
    }
    @PostMapping("/paymentRegisterParking")
    public ResponseEntity<?> paymentRegisterParking(HttpServletRequest req){
        try {
            return ResponseEntity.ok(parkingService.paymentParkingRegister(req));
        }catch (ApiRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Fail to create payment"));
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
            parkingService.updateParkingStatusID(parkingStatusID);
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

    @PutMapping("/closeParkingStatus")
    public ResponseEntity<MessageResponse> closeParkignStatus(){
        try{
            parkingService.updateParkingStatusID(5);
            return ResponseEntity.ok(new MessageResponse("Update status successfully"));
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

    @PutMapping("/openParkingStatus")
    public ResponseEntity<MessageResponse> openParkignStatus(){
        try{
            parkingService.updateParkingStatusID(4);
            return ResponseEntity.ok(new MessageResponse("Update status successfully"));
           }catch (ApiRequestException e){
            throw e;
        }
    }

    @PutMapping("/updateParkingSetting")
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

    @GetMapping("/getReturnPayment")
    public ResponseEntity<?> paymentReturn(
            HttpServletRequest request
    ){
        try{
            return paymentService.paymentReturn(request);
        }catch (ApiRequestException e){
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/checkPaymentPLO")
    public ResponseEntity<String> checkPaymentPLO(){
        String response = null;
        try{
            PLOTransaction ploTransaction = parkingService.checkPLOPayment();
            if(ploTransaction == null){
                response = "PLO has not paid the yard registration fee!";
            }
            response = "PLO has paid the parking registration fee!";
            return ResponseEntity.ok(response);
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
