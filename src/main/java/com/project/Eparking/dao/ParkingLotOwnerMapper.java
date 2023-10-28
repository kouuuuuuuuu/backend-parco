package com.project.Eparking.dao;

import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.response.Response4week;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Date;
import java.util.List;

@Mapper
public interface ParkingLotOwnerMapper {

    List<PLO> getListPloByKeywordsWithPagination(String keyword, List<Integer> parkingStatus, int pageNum, int pageSize);

    PLO getPloById(String ploId);

    List<PLO> getListPloByParkingStatusWithPagination(List<Integer> parkingStatus, int pageNum, int pageSize, String keywords);

    void updateParkingStatusByPloId(PLO plo);

    Integer countRecords(List<Integer> parkingStatus, String keywords);

    void updatePloBalanceById(String ploID, double balance);
    Response4week countRecordsByWeekPLO(Date inputDate);

    void updatePloBalanceAndCurrentSlotById(String ploID, double balance, int currentSlot);
}
