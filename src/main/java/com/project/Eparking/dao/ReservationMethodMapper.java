package com.project.Eparking.dao;

import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ReservationMethodMapper {
    List<ReservationMethod> getAllReservationMethod();
    void updateStatusReservation(RequestUpdateStatusReservation reservation,int statusID);
    void updateCheckoutReservation(RequestUpdateStatusReservation reservation, Timestamp checkOut);
    void updateCheckinReservation(RequestUpdateStatusReservation reservation, Timestamp checkIn);
}
