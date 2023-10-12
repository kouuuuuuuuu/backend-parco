package com.project.Eparking.service.impl;

import com.project.Eparking.dao.ReservationMethodMapper;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMethodMapper reservationMethod;
    @Override
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            reservationMethod.updateStatusReservation(reservation,4);
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed to change password user" + e.getMessage());
        }
        return response;
    }
}
