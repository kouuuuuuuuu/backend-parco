package com.project.Eparking.service.interf;


import com.project.Eparking.domain.dto.*;

import com.project.Eparking.domain.request.RequestFindParkingList;
import com.project.Eparking.domain.request.RequestMonthANDYear;

import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.*;

import java.util.List;

import java.text.ParseException;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservationByReservationID(int reservationID);
    String checkOutStatusReservationByReservationID(int reservationID);
    List<ReservationDTO> getReservationHistory();
    ReservationDetailDTO getReservationDetailHistory(int reservationID);
    List<Top5CustomerDTO> getTop5Customer(RequestMonthANDYear requestMonthANDYear) throws ParseException;
    List<ResponseTop5Parking> getTop5Parking(RequestMonthANDYear requestMonthANDYear);
    List<ResponseTop5Revenue> getTop5Revenue(RequestMonthANDYear requestMonthANDYear);
    ReservationInforDTO getInforReservationByLicensesPlate(String licensePlate);
    List<ResponseFindParkingList> nearestParkingList(RequestFindParkingList findParkingList);
    List<ResponseFindParkingList> cheapestParkingList(RequestFindParkingList findParkingList);
    boolean cancelReservationByID(int reservationID);
    String bookingReservation(BookingReservationDTO bookingReservationDTO);
    ResponseScreenReservation getScreenCustomer();
    List<ResponseMethodByTime> getListMethodByTime(String ploID);
    BookingDetailDTO bookingDetail(String ploID);
    String checkOutWithoutCheckCondition(int reservationID);
}
