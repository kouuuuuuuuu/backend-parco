package com.project.Eparking.service.interf;

import com.project.Eparking.domain.request.RequestUpdateStatusReservation;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservationByReservationID(int reservationID);
    String checkOutStatusReservationByReservationID(int reservationID);
}
