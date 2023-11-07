package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.CreateMotorbikeDTO;
import com.project.Eparking.domain.dto.MotorbikeDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.MotorbikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/motorbike")
@RequiredArgsConstructor
public class MotorbikeController {

    private final MotorbikeService motorbikeService;

    @GetMapping("/getLicensePlate")
    public Response getListLicensePlate() {
        try {
            List<MotorbikeDTO> motorbikeDTOS = motorbikeService.getListLicensePlate();
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_LICENSE_PLATE_SUCCESS, motorbikeDTOS);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.GET_LIST_LICENSE_PLATE_FAIL, null);
        }
    }

    @DeleteMapping("/deleteLicensePlate")
    public ResponseEntity<?> deleteLicensePlateByLicensePlateID(@RequestParam("motorbikeID") int motorbikeID) {
        try {
            boolean isDeleteSuccess = motorbikeService.deleteLicensePlateByLicensePlateID(motorbikeID);
            if (!isDeleteSuccess){
                return ResponseEntity.badRequest().body(Message.DELETE_LICENSE_PLATE_FAIL);
            }
            return ResponseEntity.ok().body(Message.DELETE_LICENSE_PLATE_SUCCESS);
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body(Message.ERROR_DELETED_LICENSE_PLATE);
        }
    }


    @PostMapping("/addLicensePlate")
    public ResponseEntity<?> addLicensePlate(@RequestBody CreateMotorbikeDTO motorbikeDTO){
        try {
            String message = motorbikeService.addLicensePlate(motorbikeDTO);
            if (message.equals(Message.DUPLICATE_LICENSE_PLATE)){
                return ResponseEntity.badRequest().body(message);
            }
            return ResponseEntity.ok().body(message);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(Message.ERROR_ADD_LICENSE_PLATE);
        }
    }
}
