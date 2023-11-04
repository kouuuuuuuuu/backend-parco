package com.project.Eparking.controller;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.RatingResponse;
import com.project.Eparking.domain.response.ResponsePLOProfile;
import com.project.Eparking.domain.response.ResponseRevenuePLO;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.RatingService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/PLO")
@RequiredArgsConstructor
public class ParkingOwnerController {
    private final RatingService ratingService;
    private final UserService userService;
    private final ParkingService parkingService;
    @GetMapping("/getRatingList")
    public ResponseEntity<List<RatingResponse>> getListRating(){
        try{
            return ResponseEntity.ok(ratingService.getRatingListByPLOID());
        }catch (Exception e){
            throw e;
        }
    }
//    @GetMapping("/profile")
//    public ResponseEntity<ResponsePLOProfile> responsePLOProfile(){
//        try{
//            return ResponseEntity.ok(userService.getPLOProfileResponseByPLOID());
//        }catch (Exception e){
//            throw e;
//        }
//    }
//    @PutMapping("/updateProfile")
//    public ResponseEntity<ResponsePLOProfile> updateProfilePLO(@RequestBody RequestPLOupdateProfile profile){
//        try{
//            return ResponseEntity.ok(userService.updatePLOprofile(profile));
//        }catch (Exception e){
//            throw e;
//        }
//    }
//    @PutMapping("/changePassword")
//    public ResponseEntity<List<String>> changePasswordUser(@RequestBody RequestChangePasswordUser password){
//        try{
//            return ResponseEntity.ok(userService.changePasswordUser(password));
//        }catch (Exception e){
//            throw e;
//        }
//    }

    @GetMapping("/getParkingInformation")
    public ResponseEntity<ParkingInformation> getParkingInformation(){
        try{
            return ResponseEntity.ok(parkingService.getParkingInformation());
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/updateParkingInformation")
    public ResponseEntity<ParkingInformation> updateParkingInformation(@RequestBody RequestUpdateProfilePLOTime plo){
        try {
            return ResponseEntity.ok(parkingService.updateParkingInformation(plo));
        }catch (Exception e){
            throw e;
        }
    }
    @GetMapping("/checkPLOTransfer")
    public ResponseEntity<String> checkPLOTransfer(@RequestParam RequestTransferParking phoneNumber){
        try{
            return ResponseEntity.ok(parkingService.checkPLOTransfer(phoneNumber));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/checkOTPcodeTransferParking")
    public ResponseEntity<String> checkOTPcodeTransferParking(RequestCheckOTPTransferParking transferParking){
        try{
            return ResponseEntity.ok(parkingService.checkOTPcodeTransferParking(transferParking));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getBalance")
    public ResponseEntity<Double> getBalance(){
        try {
            return ResponseEntity.ok(userService.getBalancePlO());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getRevenue")
    public ResponseEntity<ResponseRevenuePLO> getRevenue(){
        try {
            return ResponseEntity.ok(parkingService.getRevenuePLO());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PostMapping("/requestWithdrawal")
    public ResponseEntity<String> requestWithdrawal(@RequestBody RequestWithdrawal requestWithdrawal){
        try{
            return ResponseEntity.ok(parkingService.withdrawalRequest(requestWithdrawal));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getSumByDate")
    public ResponseEntity<Double> getSumByDate(@RequestParam Date startTime,@RequestParam Date startTime2nd){
        try {
            RequestGetSumPLO plo = new RequestGetSumPLO(startTime,startTime2nd);
            return ResponseEntity.ok(parkingService.getSumReservation(plo));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
