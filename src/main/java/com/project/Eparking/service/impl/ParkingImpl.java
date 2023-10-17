package com.project.Eparking.service.impl;


import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;


import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ESMService;
import com.project.Eparking.service.interf.ParkingService;
import com.project.Eparking.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ParkingImpl implements ParkingService {
    private final UserMapper userMapper;
    private final ParkingMapper parkingMapper;
    private final ImageMapper imageMapper;
    private final ReservationMethodMapper reservationMethodMapper;

    private final ESMService esmService;
    private final PaymentService paymentService;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public Map<String, Object> addParking(RequestRegisterParking registerParking, HttpServletRequest req) {
        try {
            RequestParking requestParking = new RequestParking();
            requestParking.setPloID(registerParking.getPloID());
            requestParking.setParkingName(registerParking.getParkingName());
            requestParking.setLength(registerParking.getLength());
            requestParking.setWidth(registerParking.getWidth());
            requestParking.setSlot(registerParking.getSlot());
            requestParking.setAddress(registerParking.getAddress());
            requestParking.setDescription(registerParking.getDescription());
            requestParking.setParkingStatusID(2);
            parkingMapper.registerParking(requestParking);
//            List<String> imageLinks = registerParking.getImages();
//            List<RequestImage> requestImages = new ArrayList<>();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            if (!registerParking.getImages().isEmpty()) {
                imageMapper.deleteImageByPLOID(id);
                List<Image> images = new ArrayList<>();
                for (String image :
                        registerParking.getImages()) {
                    images.add(new Image(0, id, image));
                }
                imageMapper.batchInsertImages(images);
            }
            Map<String, Object> responseAddParking = new HashMap<>();
            Payment payment = new Payment();
            payment.setAmountParam("350000");
            ResponseEntity<?> paymentResponse = paymentService.createPayment(req,payment);
            responseAddParking.put("Message","Register parking successfully");
            responseAddParking.put("paymentResponse",paymentResponse);
            return responseAddParking;
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
    public void updateParkingStatusID( int parkingStatusID) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            parkingMapper.updateParkingStatusID(id, parkingStatusID);
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
    public ParkingInformation updateParkingInformation(RequestUpdateProfilePLOTime plo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            RequestUpdateProfilePLO profilePLO = new RequestUpdateProfilePLO();
            profilePLO.setParkingName(plo.getParkingName());
            profilePLO.setDescription(plo.getDescription());
            profilePLO.setSlot(plo.getSlot());

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, plo.getWaitingTime().getHours());
            calendar.set(Calendar.MINUTE, plo.getWaitingTime().getMinutes());
            calendar.set(Calendar.SECOND, plo.getWaitingTime().getSeconds());
            long timeInMillis = calendar.getTimeInMillis();
            Time waitingTime = new Time(timeInMillis);
            profilePLO.setWaitingTime(waitingTime);

            Calendar calendarCancel = Calendar.getInstance();
            calendarCancel.set(Calendar.HOUR_OF_DAY, plo.getCancelBookingTime().getHours());
            calendarCancel.set(Calendar.MINUTE, plo.getCancelBookingTime().getMinutes());
            calendarCancel.set(Calendar.SECOND, plo.getCancelBookingTime().getSeconds());
            long timeInMillisCancel = calendarCancel.getTimeInMillis();
            Time cancelTime = new Time(timeInMillisCancel);
            profilePLO.setCancelBookingTime(cancelTime);

            parkingMapper.updateParkingProfile(profilePLO, id);
            if (!plo.getImage().isEmpty()) {
                imageMapper.deleteImageByPLOID(id);
                List<Image> images = new ArrayList<>();
                for (String image :
                        plo.getImage()) {
                    images.add(new Image(0, id, image));
                }
                imageMapper.batchInsertImages(images);
            }
            return getParkingInformation();
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update parking information" + e.getMessage());
        }
    }

    @Override
    public ResponseReservationDetail getReservationDetailByPLOID(int reservationID) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            return parkingMapper.getReservationDetailByReservationID(reservationID);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get parking information" + e.getMessage());
        }
    }

    @Override

    public String checkPLOTransfer(RequestTransferParking requestTransferParking) {
        String response = "";
            try{
                ResponsePLO existingPLO = userMapper.getPLOResponseByPhonenumber(requestTransferParking.getPhoneNumberTransfer());
                if(existingPLO != null){
                    return response = "The PLO is already exists";
                }
                ResponseSendOTP responseSendOTP = esmService.sendOTP(requestTransferParking.getPhoneNumberTransfer());
                if(!responseSendOTP.getCodeResult().equals("100")){
                    return response = "Can not send OTP to user";
                }
                response = "Send OTP successfully";
            }catch (Exception e){
                throw new ApiRequestException("Failed to send OTP to phoneNumber plo: " + e.getMessage());
            }
        return response;
    }

    @Override
    public String checkOTPcodeTransferParking(RequestCheckOTPTransferParking transferParking) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseCheckOTP responseCheckOTP = esmService.checkOTP(transferParking.getPhoneNumber(), transferParking.getOTPcode());
            if (responseCheckOTP.getCodeResult().equalsIgnoreCase("100")) {
                ParamTransferParking paramTransferParking = new ParamTransferParking(transferParking.getPhoneNumber(),id,"PL"+transferParking.getPhoneNumber());
                parkingMapper.updateParkingOwner(paramTransferParking);
                return "Successful owner conversion!";
            } else {
                return "OTP code is invalid";
            }
        }catch (Exception e) {
            throw new ApiRequestException("Failed to check the OTP code" + e.getMessage());
        }
    }
    @Override
    public ResponseParkingSettingWithID getParkingSettingByPLOID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<ResponseParkingSetting> settings = parkingMapper.getParkingSettingByPLOID(id);
            ResponseParkingSettingWithID parkingSettingWithID = new ResponseParkingSettingWithID(id,settings);
            return parkingSettingWithID;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get parking setting" + e.getMessage());
        }
    }
    @Transactional
    @Override
    public void settingParking(List<RequestParkingSetting> settings) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<RequestParkingSettingMapper> settingMappers = new ArrayList<>();
            if(!settings.isEmpty()){
                for (RequestParkingSetting setting:
                        settings) {
                    settingMappers.add(new RequestParkingSettingMapper(setting.getMethodID(),setting.getPrice(),id));
                }
                parkingMapper.deleteParkingSetting(id);
                parkingMapper.batchInsertSettingMethod(settingMappers);
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to update parking setting" + e.getMessage());
        }
    }

    @Override
    public List<ReservationMethod> getAllReservationMethod() {
        try{
            return reservationMethodMapper.getAllReservationMethod();
        }catch (Exception e){
            throw new ApiRequestException("Failed to get reservation method" + e.getMessage());
        }
    }

    @Override
    public PLOTransaction checkPLOPayment() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            RequestGetTransactionPLOByID transactionPLOByID = new RequestGetTransactionPLOByID(id,1);
            return transactionMapper.getTransactionPLOByID(transactionPLOByID);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get reservation method" + e.getMessage());
        }
    }
}


