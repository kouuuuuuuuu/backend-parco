package com.project.Eparking.dao;

import com.project.Eparking.domain.PLO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParkingLotOwnerMapper {

    List<PLO> getAllPloWithPagination(int pageNum, int pageSize);

    List<PLO> getListPloByStatusWithPagination(int status, int pageNum, int pageSize);

    List<PLO> getListPloByKeywordsWithPagination(String keyword, int pageNum, int pageSize);
}
