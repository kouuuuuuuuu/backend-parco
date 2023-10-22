package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.Top5CustomerDTO;
import com.project.Eparking.domain.request.RequestMothANDYear;
import com.project.Eparking.domain.request.RequestMonthANDYear;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.ResponseTop5Parking;
import com.project.Eparking.domain.response.ResponseTop5Revenue;

import java.util.List;

import java.text.ParseException;
import java.util.List;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservationByReservationID(int reservationID);
    String checkOutStatusReservationByReservationID(int reservationID);
    List<Top5CustomerDTO> getTop5Customer(RequestMothANDYear requestMonthANDYear) throws ParseException;
    List<ResponseTop5Parking> getTop5Parking(RequestMonthANDYear requestMonthANDYear);
    List<ResponseTop5Revenue> getTop5Revenue(RequestMonthANDYear requestMonthANDYear);
}
