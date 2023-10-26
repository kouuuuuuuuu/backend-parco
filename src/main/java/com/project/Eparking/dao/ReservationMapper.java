package com.project.Eparking.dao;


import com.project.Eparking.domain.Reservation;

import com.project.Eparking.domain.dto.Top5CustomerDTO;

import com.project.Eparking.domain.response.*;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ReservationMapper {
    ResponseReservation findReservationByLicensePlate(String ploID,int status,String licensePlate);
    Double sumPriceReservationCurrentDateByPLO(String ploID);
    ResponseRevenuePLO getReservationMethodByMethodID(String ploID);
    Double getSumByDateANDPLOID(Date startTime,Date startTime2th,String ploID);

    List<Reservation> getReservationByStatus(int status, String customerID);

    Reservation getReservationDetailById(int reservationID, String customerID);
  
    List<Top5CustomerDTO> getTop5CustomerHaveMostReservation(Date sqlDate);
    List<ResponseTop5Parking> getTop5ParkingHaveMostReservation(Date inputDate);
    List<ResponseTop5Revenue> getTop5ParkingHaveHighestRevenue(Date inputDate);
    Reservation getReservationByReservationID(int reservationID);
    List<ResponseCoordinates> getAllCoordinatesPLO();
}
