package com.project.Eparking.dao;

import com.project.Eparking.domain.ParkingMethod;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParkingMethodMapper {
    List<ParkingMethod> getParkingMethodById(String ploId);
}
