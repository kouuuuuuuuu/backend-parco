package com.project.Eparking.service.impl;

import com.project.Eparking.dao.*;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingLotOwnerServiceImpl implements ParkingLotOwnerService {

    private final ParkingLotOwnerMapper parkingLotOwnerMapper;

    @Override
    public List<ListPloDTO> getListPloByStatus(int status, int pageNum, int pageSize) {
        List<ListPloDTO> parkingLotOwnerDTOList = new ArrayList<>();
        List<PLO> ploList;
        int pageNumOffset = pageNum == 0 ? 0 : (pageNum -1) * pageSize;
        //1. Get list PLO by status from database
        if (status == 0){
            ploList = parkingLotOwnerMapper.getAllPloWithPagination(pageNumOffset, pageSize);
        }else {
            ploList = parkingLotOwnerMapper.getListPloByStatusWithPagination(status, pageNumOffset, pageSize);
        }


        //2. Mapping data from entity to dto
        for (PLO plo : ploList){
            ListPloDTO ploDto = new ListPloDTO();
            ploDto.setPloID(plo.getPloID());
            ploDto.setAddress(plo.getAddress());
            ploDto.setPhoneNumber(plo.getPhoneNumber());
            ploDto.setParkingName(plo.getParkingName());
            ploDto.setFullName(plo.getFullName());
            parkingLotOwnerDTOList.add(ploDto);
        }
        return parkingLotOwnerDTOList;
    }

    @Override
    public List<ListPloDTO> getListPloByKeywords(String keyword, int pageNum, int pageSize) {

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list plo entity by keyword
        List<PLO> ploList = parkingLotOwnerMapper.getListPloByKeywordsWithPagination("%" +keyword + "%", pageNumOffset, pageSize);

        //2. Mapping data to dto
        List<ListPloDTO> listPloDTOS = new ArrayList<>();
        for (PLO plo : ploList){
            ListPloDTO listPloDTO = new ListPloDTO();
            listPloDTO.setPloID(plo.getPloID());
            listPloDTO.setFullName(plo.getFullName());
            listPloDTO.setParkingName(plo.getParkingName());
            listPloDTO.setAddress(plo.getAddress());
            listPloDTO.setPhoneNumber(plo.getPhoneNumber());
            listPloDTOS.add(listPloDTO);
        }

        return listPloDTOS;
    }

}
