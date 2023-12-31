package com.project.Eparking.controller;

import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.response.MessageResponse;
import com.project.Eparking.domain.response.ResponseParkingStatus;
import com.project.Eparking.domain.response.ResponseRegisterParking;
import com.project.Eparking.domain.response.ResponseShowVehicleInParking;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
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
    public ResponseEntity<List<ResponseShowVehicleInParking>> showListVehicleInParking(
            @RequestParam String ploID,
            @RequestParam int parkingStatusID,
            HttpServletResponse response,
            HttpServletRequest request) {
        try {
            List<ResponseShowVehicleInParking> responseShowVehicleInParking = parkingService.showListVehicleInParking(ploID, parkingStatusID);
            return ResponseEntity.ok(responseShowVehicleInParking);
        } catch (ApiRequestException e) {
            throw e;
        }
    }

}
