package com.project.Eparking.dao;

import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.ResponseMethod;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ReservationMethodMapper {
    List<ReservationMethod> getAllReservationMethod();
    void updateStatusReservation(int reservationID,int statusID);
    void updateCheckoutReservation(int reservationID, Timestamp checkOut);
    void updateCheckinReservation(int reservationID, Timestamp checkIn);
    ReservationMethod getReservationMethodById(int methodID);
    ReservationMethod getMethodByTime(Timestamp currentTime);
    List<ResponseMethod> getMethodByID(String ploID);
}
