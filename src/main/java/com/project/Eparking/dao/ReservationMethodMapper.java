package com.project.Eparking.dao;

import com.project.Eparking.domain.ReservationMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationMethodMapper {
    List<ReservationMethod> getAllReservationMethod();
}
