package com.project.Eparking.dao;

import com.project.Eparking.domain.dto.Top5CustomerDTO;
import com.project.Eparking.domain.response.ResponseReservation;
import com.project.Eparking.domain.response.ResponseRevenuePLO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ReservationMapper {
    ResponseReservation findReservationByLicensePlate(String ploID,int status,String licensePlate);
    Double sumPriceReservationCurrentDateByPLO(String ploID);
    ResponseRevenuePLO getReservationMethodByMethodID(String ploID);
    Double getSumByDateANDPLOID(Date startTime,Date startTime2th,String ploID);

    List<Top5CustomerDTO> getTop5CustomerHaveMostReservation(Date sqlDate);
}
