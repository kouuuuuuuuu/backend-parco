package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.dto.ReservationDTO;
import com.project.Eparking.domain.dto.ReservationDetailDTO;
import com.project.Eparking.domain.request.RequestUpdateReservation;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    @PutMapping("/checkoutReservation")
    public ResponseEntity<String> checkoutStatusReservation(@RequestBody RequestUpdateReservation reservation){
        try{
            return ResponseEntity.ok(reservationService.checkOutStatusReservationByReservationID(reservation.getReservationID()));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/checkinReservation")
    public ResponseEntity<String> checkinStatusReservation(@RequestBody RequestUpdateReservation reservation){
        try{
            return ResponseEntity.ok(reservationService.checkInStatusReservationByReservationID(reservation.getReservationID()));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @PutMapping("/checkoutReservationWithLicensePlate")
    public ResponseEntity<String> checkoutStatusReservationWithLicensePlate(@RequestBody RequestUpdateStatusReservation reservation){
        try{
            return ResponseEntity.ok(reservationService.checkOutStatusReservation(reservation));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @PutMapping("/checkinReservationWithLicensePlate")
    public ResponseEntity<String> checkinStatusReservationWithLicensePlate(@RequestBody RequestUpdateStatusReservation reservation){
        try{
            return ResponseEntity.ok(reservationService.checkInStatusReservation(reservation));
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @GetMapping("/reservationHistory")
    public Response getReservationHistory(){
        try {
            List<ReservationDTO> reservationDTOS = reservationService.getReservationHistory();
            if (reservationDTOS.isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.RESERVATION_HISTORY_EMPTY, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_RESERVATION_HISTORY_SUCCESS, reservationDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_GET_RESERVATION_HISTORY, null);
        }
    }

    @GetMapping("/reservationHistoryDetail")
    public Response getReservationDetailHistory(@RequestParam("reservationId") int reservationID){
        try {
            ReservationDetailDTO reservationDetailDTO = reservationService.getReservationDetailHistory(reservationID);
            if (Objects.isNull(reservationDetailDTO)){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.RESERVATION_HISTORY_EMPTY, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_RESERVATION_HISTORY_SUCCESS, reservationDetailDTO);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_GET_RESERVATION_HISTORY, null);
        }
    }
}
