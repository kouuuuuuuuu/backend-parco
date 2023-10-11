package com.project.Eparking.controller;

import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    @PutMapping("/checkoutReservation")
    public ResponseEntity<String> checkouStatusReservation(@RequestBody RequestUpdateStatusReservation reservation){
        try{
            return ResponseEntity.ok(reservationService.checkOutStatusReservation(reservation));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
