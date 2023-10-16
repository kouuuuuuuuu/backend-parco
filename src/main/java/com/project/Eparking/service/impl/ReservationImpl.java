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

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMethodMapper reservationMethodMapper;
    @Override
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            reservationMethodMapper.updateStatusReservation(reservation,4);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckoutReservation(reservation,timestamp);
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed to change password user" + e.getMessage());
        }
        return response;
    }

    @Override
    public String checkInStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            reservationMethodMapper.updateStatusReservation(reservation,2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(reservation,timestamp);;
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed to change password user" + e.getMessage());
        }
        return response;
    }
}
