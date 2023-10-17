package com.project.Eparking.dao;

import com.project.Eparking.domain.ParkingStatus;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingStatusMapper {
    ParkingStatus getByParkingStatusId(int parkingStatusID);
}
