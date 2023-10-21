package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.ReservationDTO;
import com.project.Eparking.domain.dto.ReservationDetailDTO;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;

import java.util.List;

public interface ReservationService {
    String checkOutStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservation(RequestUpdateStatusReservation reservation);
    String checkInStatusReservationByReservationID(int reservationID);
    String checkOutStatusReservationByReservationID(int reservationID);

    List<ReservationDTO> getReservationHistory();

    ReservationDetailDTO getReservationDetailHistory(int reservationID);
}
