package com.project.Eparking.service.impl;


import com.project.Eparking.dao.ParkingMapper;
import com.project.Eparking.domain.request.RequestImage;
import com.project.Eparking.domain.request.RequestParking;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ParkingImpl implements ParkingService {
    private final ParkingMapper parkingMapper;

    @Override
    @Transactional
    public void addParking(RequestRegisterParking registerParking) {
        try {
            RequestParking requestParking = new RequestParking();
            requestParking.setPloID(registerParking.getPloID());
            requestParking.setParkingName(registerParking.getParkingName());
            requestParking.setLength(registerParking.getLength());
            requestParking.setWidth(registerParking.getWidth());
            requestParking.setSlot(registerParking.getSlot());
            requestParking.setAddress(registerParking.getAddress());
            requestParking.setDescription(registerParking.getDescription());
            requestParking.setImageLinkTransaction(registerParking.getImageLinkTransaction());
            requestParking.setParkingStatusID(2);
            parkingMapper.registerParking(requestParking);

            List<String> imageLinks = registerParking.getImages();
            List<RequestImage> requestImages = new ArrayList<>();
            for (String imageLink : imageLinks) {
                RequestImage requestImage = new RequestImage();
                requestImage.setPloID(registerParking.getPloID());
                requestImage.setImageLink(imageLink);
                requestImages.add(requestImage);
                parkingMapper.addImage(requestImage);
            }
        } catch (Exception e) {
            throw new ApiRequestException("Failed to register parking: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getParkingStatusOrList(String ploID) {
        try {
            ResponseParkingStatus parkingStatus = parkingMapper.getParkingStatus(ploID);
            if (parkingStatus.getParkingStatusID() == 4 || parkingStatus.getParkingStatusID() == 5) {
                ResponseParkingList responseParkingList = new ResponseParkingList();
                List<ParkingComing> listParkingOngoing = parkingMapper.getListParkingOngoing();
                responseParkingList.setTotalComing(listParkingOngoing);
                responseParkingList.setTotalVehicle(listParkingOngoing.size());
                responseParkingList.setParkingStatusID(parkingStatus.getParkingStatusID());
                responseParkingList.setStatusName(parkingStatus.getStatusName());
                return ResponseEntity.ok(responseParkingList);
            } else {
                return ResponseEntity.ok(parkingStatus);
            }
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateParkingStatusID(String ploID, int parkingStatusID) {
        try {
            parkingMapper.updateParkingStatusID(ploID, parkingStatusID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get parking status: " + e.getMessage());
        }
    }

    @Override
    public List<ResponseShowVehicleInParking> showListVehicleInParking(String ploID, int parkingStatusID) {
        try {
            List<ResponseShowVehicleInParking> responseShowVehicleInParking = parkingMapper.showListVehicleInParking(ploID, parkingStatusID);
            return responseShowVehicleInParking;
        } catch (ApiRequestException e) {
            throw e;
        }
    }


}


