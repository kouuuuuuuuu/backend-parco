package com.project.Eparking.service.interf;

import com.project.Eparking.domain.request.RequestMonthANDYear;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.ResponseTop5Parking;

import java.util.List;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservationByReservationID(int reservationID);
    String checkOutStatusReservationByReservationID(int reservationID);
    List<ResponseTop5Parking> getTop5Parking(RequestMonthANDYear requestMonthANDYear);
}
