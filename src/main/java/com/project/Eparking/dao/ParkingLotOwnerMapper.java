package com.project.Eparking.dao;

import com.project.Eparking.domain.PLO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ParkingLotOwnerMapper {
    
    PLO getPloById(String ploId);
}
