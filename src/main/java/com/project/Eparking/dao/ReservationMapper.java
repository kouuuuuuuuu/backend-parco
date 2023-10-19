package com.project.Eparking.dao;

import com.project.Eparking.domain.response.ResponseReservation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationMapper {
    ResponseReservation findReservationByLicensePlate(String ploID,int status,String licensePlate);
    Double sumPriceReservationCurrentDateByPLO(String ploID);
}
