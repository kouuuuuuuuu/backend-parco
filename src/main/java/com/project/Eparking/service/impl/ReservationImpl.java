package com.project.Eparking.service.impl;

import com.project.Eparking.dao.ParkingMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.dao.ReservationMethodMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.ResponseReservation;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMethodMapper reservationMethodMapper;
    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final ParkingMapper parkingMapper;
    @Override
    @Transactional
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id,2,reservation.getLicensePlate());
            if(responseReservation == null){
                return "Dont have any reservation with license plate";
            }
            reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(),4);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(),timestamp);
            PLO plo = userMapper.getPLOByPLOID(responseReservation.getPloID());
            int currentSlot = plo.getCurrentSlot() - 1;
            if(currentSlot < 0){
                return "Something error with currentSlot plo";
            }
            parkingMapper.updateCurrentSlot(currentSlot,plo.getPloID());
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed checkIn user" + e.getMessage());
        }
        return response;
    }
    @Transactional
    @Override
    public String checkInStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id,1,reservation.getLicensePlate());
            if(responseReservation == null){
                return "Dont have any reservation with license plate";
            }
            reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(responseReservation.getReservationID(),timestamp);;
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed checkIn user" + e.getMessage());
        }
        return response;
    }

    @Override
    public String checkInStatusReservationByReservationID(int reservationID) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            reservationMethodMapper.updateStatusReservation(reservationID, 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(reservationID,timestamp);
            return "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed checkIn user" + e.getMessage());
        }
    }

    @Override
    public String checkOutStatusReservationByReservationID(int reservationID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        reservationMethodMapper.updateStatusReservation(reservationID,4);
        long epochMilli = Instant.now().toEpochMilli();
        Timestamp timestamp = new Timestamp(epochMilli);
        reservationMethodMapper.updateCheckoutReservation(reservationID,timestamp);
        PLO plo = userMapper.getPLOByPLOID(id);
        int currentSlot = plo.getCurrentSlot() - 1;
        if(currentSlot < 0){
            return "Something error with currentSlot plo";
        }
        parkingMapper.updateCurrentSlot(currentSlot,plo.getPloID());
        return "Update successfully!";
    }
}
