package com.project.Eparking.service.impl;

import com.project.Eparking.dao.*;
import com.project.Eparking.domain.Image;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.ParkingMethod;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.dto.ImageDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
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

    private final ImageMapper imageMapper;

    private final ParkingMethodMapper parkingMethodMapper;

    private final ReservationMethodMapper reservationMethodMapper;
    @Override
    public ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception {
        //1. Get Image by ploId
        List<Image> images = imageMapper.getImageByPloId(ploId);

        //3. Get Plo by ploId;
        PLO ploEntity = parkingLotOwnerMapper.getPloById(ploId);

        //4. Get Fee by parking method
        List<ParkingMethod> parkingMethod = parkingMethodMapper.getParkingMethodById(ploId);
        ParkingMethod morningMethod = new ParkingMethod();
        ParkingMethod eveningMethod = new ParkingMethod();
        ParkingMethod overnightMethod = new ParkingMethod();

        List<ReservationMethod> reservationMethods = reservationMethodMapper.getAllReservationMethod();
        for (ReservationMethod reservationMethod : reservationMethods){
            if (reservationMethod.getMethodName().equals("Morning")){
                morningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodName().equals("Evening")){
                eveningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodName().equals("Overnight")){
                overnightMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }
        }
        
        List<ImageDTO> imageDTOS = new ArrayList<>();
        //4. Mapping to dto
        if (!images.isEmpty()){
            for (Image image : images){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageID(image.getImageID());
                imageDTO.setImageLink(image.getImageLink());
                imageDTOS.add(imageDTO);
            }
        }

        ParkingLotOwnerDTO parkingLotOwnerDTO = new ParkingLotOwnerDTO();
        parkingLotOwnerDTO.setPloID(ploEntity.getPloID());
        parkingLotOwnerDTO.setParkingName(ploEntity.getParkingName());
        parkingLotOwnerDTO.setAddress(ploEntity.getAddress());
        parkingLotOwnerDTO.setPhoneNumber(ploEntity.getPhoneNumber());
        parkingLotOwnerDTO.setEmail(ploEntity.getEmail());
        parkingLotOwnerDTO.setContractDuration(ploEntity.getContractDuration());
        parkingLotOwnerDTO.setContractLink(ploEntity.getContractLink());
        parkingLotOwnerDTO.setStatus(ploEntity.getStatus());
        parkingLotOwnerDTO.setMorningFee(morningMethod != null ? morningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setEveningFee(eveningMethod != null ? eveningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setOverNightFee(overnightMethod != null ? overnightMethod.getPrice() : 0);
        parkingLotOwnerDTO.setSlot(ploEntity.getSlot());
        parkingLotOwnerDTO.setLatitude(ploEntity.getLatitude().toString());
        parkingLotOwnerDTO.setLongtitule(ploEntity.getLongtitude().toString());
        parkingLotOwnerDTO.setLength(ploEntity.getLength());
        parkingLotOwnerDTO.setWidth(ploEntity.getWidth());
        parkingLotOwnerDTO.setStar(ploEntity.getStar());
        parkingLotOwnerDTO.setCurrentSlot(ploEntity.getCurrentSlot());
        return parkingLotOwnerDTO;
    }

}
