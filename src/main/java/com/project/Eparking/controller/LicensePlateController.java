package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.domain.dto.LicensePlateDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.LicensePlateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/licensePlate")
@RequiredArgsConstructor
public class LicensePlateController {

    private final LicensePlateService licensePlateService;

    @GetMapping("/getLicensePlate")
    public Response getListLicensePlate() {
        try {
            List<LicensePlate> licensePlates = licensePlateService.getListLicensePlate();
            List<LicensePlateDTO> licensePlateDTOS = new ArrayList<>();
            for (LicensePlate licensePlate : licensePlates) {
                LicensePlateDTO licensePlateDTO = new LicensePlateDTO(licensePlate.getLicensePlateID(), licensePlate.getLicensePlate());
                licensePlateDTOS.add(licensePlateDTO);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_LICENSE_PLATE_SUCCESS, licensePlateDTOS);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.GET_LIST_LICENSE_PLATE_FAIL, null);
        }
    }

    @DeleteMapping("/deleteLicensePlate")
    public Response deleteLicensePlateByLicensePlateID(@RequestParam("licensePlateID") int licensePlateID) {
        try {
            boolean isDeleteSuccess = licensePlateService.deleteLicensePlateByLicensePlateID(licensePlateID);
            if (!isDeleteSuccess){
                return new Response(HttpStatus.BAD_REQUEST.value(), Message.DELETE_LICENSE_PLATE_FAIL, isDeleteSuccess);
            }
            return new Response(HttpStatus.OK.value(), Message.DELETE_LICENSE_PLATE_SUCCESS, isDeleteSuccess);
        }catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_DELETED_LICENSE_PLATE, null);
        }
    }


    @PostMapping("/addLicensePlate")
    public Response addLicensePlate(@RequestParam("licensePlate") String licensePlate){
        try {
            String message = licensePlateService.addLicensePlate(licensePlate);
            return new  Response(HttpStatus.OK.value(), message , null);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_ADD_LICENSE_PLATE, null);
        }
    }
}
