package com.project.Eparking.service.interf;

import com.project.Eparking.domain.request.RequestUpdateStatusReservation;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
}