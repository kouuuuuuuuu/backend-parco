package com.project.Eparking.service.impl;


import com.project.Eparking.dao.ImageMapper;
import com.project.Eparking.dao.ParkingMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.Image;
import com.project.Eparking.domain.ParkingInformation;
import com.project.Eparking.domain.request.RequestImage;
import com.project.Eparking.domain.request.RequestParking;
import com.project.Eparking.domain.request.RequestRegisterParking;
import com.project.Eparking.domain.request.RequestUpdateProfilePLO;
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
    private final UserMapper userMapper;
    private final ParkingMapper parkingMapper;
    private final ImageMapper imageMapper;

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
    public List<ResponseShowVehicleInParking> showListVehicleInParking(int parkingStatusID) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<ResponseShowVehicleInParking> responseShowVehicleInParking = parkingMapper.showListVehicleInParking(id, parkingStatusID);
            return responseShowVehicleInParking;
        } catch (ApiRequestException e) {
            throw e;
        }
    }

    @Override
    public ParkingInformation getParkingInformation() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ParkingInformation parkingInformation = userMapper.getParkingInformationByPLOID(id);
            parkingInformation.setImage(imageMapper.getImageListByPLOID(id));
            return parkingInformation;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get parking information" + e.getMessage());
        }
    }
    @Transactional
    @Override
    public ParkingInformation updateParkingInformation(RequestUpdateProfilePLO plo) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            parkingMapper.updateParkingProfile(plo,id);
            if(!plo.getImage().isEmpty()){
                imageMapper.deleteImageByPLOID(id);
                List<Image> images = new ArrayList<>();
                for (String image :
                     plo.getImage()) {
                    images.add(new Image(0,id,image));
                }
                imageMapper.batchInsertImages(images);
            }
            return getParkingInformation();
        }catch (Exception e){
            throw new ApiRequestException("Failed to update parking information" + e.getMessage());
        }
    }
}


