package com.project.Eparking.service.impl;


import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.dto.ReservationDTO;
import com.project.Eparking.domain.dto.ReservationDetailDTO;

import com.project.Eparking.dao.ParkingMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.dao.ReservationMethodMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.dto.ReservationInforDTO;
import com.project.Eparking.domain.dto.Top5CustomerDTO;
import com.project.Eparking.domain.request.RequestMonthANDYear;

import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.ResponseReservation;
import com.project.Eparking.domain.response.ResponseTop5Parking;
import com.project.Eparking.domain.response.ResponseTop5Revenue;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMethodMapper reservationMethodMapper;
    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final ParkingMapper parkingMapper;
    private final ParkingLotOwnerMapper parkingLotOwnerMapper;
    private final LicensePlateMapper licensePlateMapper;
    private final ReservationStatusMapper reservationStatusMapper;
    private final CustomerMapper customerMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
    @Override
    @Transactional
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id,2,reservation.getLicensePlate());
            if(responseReservation == null){
                responseReservation = reservationMapper.findReservationByLicensePlate(id,3,reservation.getLicensePlate());
            }
            if(responseReservation == null){
                throw new ApiRequestException("Dont have any reservation with license plate");
            }
            if(responseReservation.getStatusID() == 2 || responseReservation.getStatusID() == 3){
                reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(),4);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(),timestamp);
                PLO plo = userMapper.getPLOByPLOID(responseReservation.getPloID());
                int currentSlot = plo.getCurrentSlot() - 1;
                if(currentSlot < 0){
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot,plo.getPloID());
                response = "Update successfully!";
            }else {
                throw new ApiRequestException("Wrong status");
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed checkOut user." + e.getMessage());
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
                throw new ApiRequestException("Dont have any reservation with license plate");
            }
            if(responseReservation.getStatusID() != 1){
                throw new ApiRequestException("Wrong status");
            }
            reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(responseReservation.getReservationID(),timestamp);;
            response = "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed checkIn user." + e.getMessage());
        }
        return response;
    }
    @Transactional
    @Override
    public String checkInStatusReservationByReservationID(int reservationID) {
        try{
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            if(reservation.getStatusID() != 1){
                throw new ApiRequestException("Wrong status");
            }
            reservationMethodMapper.updateStatusReservation(reservationID, 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(reservationID,timestamp);
            return "Update successfully!";
        }catch (Exception e){
            throw new ApiRequestException("Failed checkIn user." + e.getMessage());
        }
    }
    @Transactional
    @Override
    public String checkOutStatusReservationByReservationID(int reservationID) {
        try {
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            int statusID = reservation.getStatusID();
            if (statusID == 2 || statusID == 3) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String id = authentication.getName();
                reservationMethodMapper.updateStatusReservation(reservationID, 4);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                reservationMethodMapper.updateCheckoutReservation(reservationID, timestamp);
                PLO plo = userMapper.getPLOByPLOID(id);
                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                return "Update successfully!";
            } else {
                throw new ApiRequestException("Wrong status");
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed checkOut user." + e.getMessage());
        }
    }

    @Override
    public List<ResponseTop5Parking> getTop5Parking(RequestMonthANDYear requestMonthANDYear) {
        try{
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            return reservationMapper.getTop5ParkingHaveMostReservation(sqlDate);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get top 5 parking have most reservation" + e.getMessage());
        }
    }

    @Override
    public List<ResponseTop5Revenue> getTop5Revenue(RequestMonthANDYear requestMonthANDYear) {
        try{
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            return reservationMapper.getTop5ParkingHaveHighestRevenue(sqlDate);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get top 5 parking have most reservation" + e.getMessage());
        }
    }

    @Override
    public ReservationInforDTO getInforReservationByLicensesPlate(String licensePlate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        int status = 4;
        Reservation reservation;
        reservation = reservationMapper.findReservationByLicensePlateAndPloId(licensePlate, id, status);
        if (Objects.isNull(reservation)){
            return null;
        }

        if (reservation.getStatusID() == 2){
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (currentTime.after(reservation.getEndTime())){
                reservationMethodMapper.updateStatusReservation(reservation.getReservationID(), 3);
                reservation = reservationMapper.findReservationByLicensePlateAndPloId(licensePlate, id, status);
            }
        }

        Customer customer = customerMapper.getCustomerById(reservation.getCustomerID());
        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        LicensePlate licensePlates = licensePlateMapper.getLicensePlateById(reservation.getLicensePlateID());

        ReservationInforDTO reservationInforDTO = new ReservationInforDTO();
        reservationInforDTO.setCustomerName(customer.getFullName());
        reservationInforDTO.setMethodName(reservationMethod.getMethodName());
        reservationInforDTO.setStatus(reservationStatus.getStatusID());
        reservationInforDTO.setStatusName(reservationStatus.getStatusName());
        reservationInforDTO.setLicensePlate(licensePlates.getLicensePlate());
        reservationInforDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn())?
                dateFormat.format(reservation.getCheckIn()) : "");
        reservationInforDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut())?
                dateFormat.format(reservation.getCheckOut()) : "");
        reservationInforDTO.setStartTime(Objects.nonNull(reservation.getStartTime())?
                dateFormat.format(reservation.getStartTime()) : "");
        reservationInforDTO.setEndTime(Objects.nonNull(reservation.getEndTime())?
                dateFormat.format(reservation.getEndTime()) : "");

        return reservationInforDTO;
    }

    @Override
    public List<Top5CustomerDTO> getTop5Customer(RequestMonthANDYear requestMonthANDYear) throws ParseException {
        Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
        java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
        return reservationMapper.getTop5CustomerHaveMostReservation(sqlDate);
    }

    @Override
    public List<ReservationDTO> getReservationHistory() {
        List<ReservationDTO> reservationDTOS = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        int status = 4;

        List<Reservation> responseReservations = reservationMapper.getReservationByStatus(status, id);
        if (responseReservations.isEmpty()){
            return reservationDTOS;
        }
        for (Reservation reservation : responseReservations){
            ReservationDTO reservationDTO = new ReservationDTO();
            PLO plo = parkingLotOwnerMapper.getPloById(reservation.getPloID());
            ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
            reservationDTO.setReservationID(reservation.getReservationID());
            reservationDTO.setAddress(plo.getAddress());
            reservationDTO.setParkingName(plo.getParkingName());
            reservationDTO.setMethodName(reservationMethod.getMethodName());
            reservationDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn())?
                   dateFormat.format(reservation.getCheckIn()) : "");
            reservationDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut())?
                    dateFormat.format(reservation.getCheckOut()) : "");
            reservationDTOS.add(reservationDTO);
        }
        return reservationDTOS;
    }

    @Override
    public ReservationDetailDTO getReservationDetailHistory(int reservationID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Reservation reservation = reservationMapper.getReservationDetailById(reservationID, id);
        if (Objects.isNull(reservation)){
            return null;
        }
        ReservationDetailDTO reservationDetailDTO = new ReservationDetailDTO();
        PLO plo = parkingLotOwnerMapper.getPloById(reservation.getPloID());
        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
        LicensePlate licensePlate = licensePlateMapper.getLicensePlateById(reservation.getLicensePlateID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        reservationDetailDTO.setFee(reservation.getPrice());
        reservationDetailDTO.setParkingName(plo.getParkingName());
        reservationDetailDTO.setAddress(plo.getAddress());
        reservationDetailDTO.setMethodName(reservationMethod.getMethodName());
        reservationDetailDTO.setLicensePlate(licensePlate.getLicensePlate());
        reservationDetailDTO.setStatusName(reservationStatus.getStatusName());
        reservationDetailDTO.setStartTime(Objects.nonNull(reservation.getStartTime())?
                dateFormat.format(reservation.getStartTime()) : "");
        reservationDetailDTO.setEndTime(Objects.nonNull(reservation.getEndTime())?
                dateFormat.format(reservation.getEndTime()) : "");
        reservationDetailDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn())?
                dateFormat.format(reservation.getCheckIn()) : "");
        reservationDetailDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut())?
                dateFormat.format(reservation.getCheckOut()) : "");

        return reservationDetailDTO;
    }
}
