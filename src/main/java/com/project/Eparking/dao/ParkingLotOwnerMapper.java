package com.project.Eparking.dao;

import com.project.Eparking.domain.PLO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ParkingLotOwnerMapper {

    List<PLO> getListPloByKeywordsWithPagination(String keyword, int pageNum, int pageSize);

    PLO getPloById(String ploId);

    List<PLO> getListPloByParkingStatusWithPagination(List<Integer> parkingStatus, int pageNum, int pageSize);
}
