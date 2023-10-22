package com.project.Eparking.dao;

import com.project.Eparking.domain.ReservationStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationStatusMapper {
    ReservationStatus getReservationStatusByID(int status);
}
