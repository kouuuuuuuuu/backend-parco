package com.project.Eparking.controller;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestPLOupdateProfile;
import com.project.Eparking.domain.response.ResponsePLOProfile;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.RatingService;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<Rating>> getListRating(){
        try{
            return ResponseEntity.ok(ratingService.getRatingListByPLOID());
        }catch (Exception e){
            throw e;
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<ResponsePLOProfile> responsePLOProfile(){
        try{
            return ResponseEntity.ok(userService.getPLOProfileResponseByPLOID());
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/updateProfile")
    public ResponseEntity<ResponsePLOProfile> updateProfilePLO(@RequestBody RequestPLOupdateProfile profile){
        try{
            return ResponseEntity.ok(userService.updatePLOprofile(profile));
        }catch (Exception e){
            throw e;
        }
    }
    @PutMapping("/changePassword")
    public ResponseEntity<List<String>> changePasswordUser(RequestChangePasswordUser password){
        try{
            return ResponseEntity.ok(userService.changePasswordUser(password));
        }catch (Exception e){
            throw e;
        }
    }

    @GetMapping("/getParkingInformation")
    public ResponseEntity<ParkingInformation> getParkingInformation(){
        try{
            return ResponseEntity.ok(parkingService.getParkingInformation());
        }catch (Exception e){
            throw e;
        }
    }
}
