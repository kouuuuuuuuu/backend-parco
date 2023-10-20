package com.project.Eparking.dao;

import com.project.Eparking.domain.response.ResponseReservation;
import com.project.Eparking.domain.response.ResponseRevenuePLO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;

@Mapper
public interface ReservationMapper {
    ResponseReservation findReservationByLicensePlate(String ploID,int status,String licensePlate);
    Double sumPriceReservationCurrentDateByPLO(String ploID);
    ResponseRevenuePLO getReservationMethodByMethodID(String ploID);
    Double getSumByDateANDPLOID(Date startTime,Date startTime2th,String ploID);
}
