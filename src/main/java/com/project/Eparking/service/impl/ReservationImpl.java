package com.project.Eparking.service.impl;


import com.project.Eparking.constant.Message;
import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.dto.*;

import com.project.Eparking.dao.ParkingMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.dao.ReservationMethodMapper;
import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.request.RequestFindParkingList;
import com.project.Eparking.domain.request.RequestMonthANDYear;

import com.project.Eparking.domain.request.RequestUpdateStatusReservation;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.LicensePlateService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHandler;


import java.math.BigDecimal;

import java.sql.Time;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationImpl implements ReservationService {
    private final ReservationMethodMapper reservationMethodMapper;
    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final ParkingMapper parkingMapper;
    private final ParkingLotOwnerMapper parkingLotOwnerMapper;
    private final LicensePlateMapper licensePlateMapper;
    private final ReservationStatusMapper reservationStatusMapper;
    private final CustomerMapper customerMapper;
    private final ParkingMethodMapper parkingMethodMapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");

    @Override
    @Transactional
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id, 2, reservation.getLicensePlate());
            if (responseReservation == null) {
                responseReservation = reservationMapper.findReservationByLicensePlate(id, 3, reservation.getLicensePlate());
            }
            if (responseReservation == null) {
                throw new ApiRequestException("Dont have any reservation with license plate");
            }
            if (responseReservation.getStatusID() == 2 || responseReservation.getStatusID() == 3) {
                reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 4);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(), timestamp);
                PLO plo = userMapper.getPLOByPLOID(responseReservation.getPloID());
                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                response = "Update successfully!";

            } else {
                throw new ApiRequestException("Wrong status");
            }
        } catch (Exception e) {
            throw new ApiRequestException("Failed checkOut user." + e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public String checkInStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id, 1, reservation.getLicensePlate());
            if (responseReservation == null) {
                throw new ApiRequestException("Dont have any reservation with license plate");
            }
            if (responseReservation.getStatusID() != 1) {
                throw new ApiRequestException("Wrong status");
            }
            reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(responseReservation.getReservationID(), timestamp);
            response = "Update successfully!";
        } catch (Exception e) {
            throw new ApiRequestException("Failed checkIn user." + e.getMessage());
        }
        return response;
    }

    @Transactional
    @Override
    public String checkInStatusReservationByReservationID(int reservationID) {
        try {
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            if (reservation.getStatusID() != 1) {
                throw new ApiRequestException("Wrong status");
            }
            reservationMethodMapper.updateStatusReservation(reservationID, 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(reservationID, timestamp);
            return "Update successfully!";
        } catch (Exception e) {
            throw new ApiRequestException("Failed checkIn user." + e.getMessage());
        }
    }

    @Transactional
    @Override
    public String checkOutStatusReservationByReservationID(int reservationID) {
        try {
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            int statusID = reservation.getStatusID();
            if (statusID == 2 || statusID == 3) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String id = authentication.getName();
                reservationMethodMapper.updateStatusReservation(reservationID, 4);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                reservationMethodMapper.updateCheckoutReservation(reservationID, timestamp);
                PLO plo = userMapper.getPLOByPLOID(id);
                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                return "Update successfully!";
            } else {
                throw new ApiRequestException("Wrong status");
            }
        } catch (Exception e) {
            throw new ApiRequestException("Failed checkOut user." + e.getMessage());
        }
    }

    @Override
    public List<ResponseTop5Parking> getTop5Parking(RequestMonthANDYear requestMonthANDYear) {
        try {
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            return reservationMapper.getTop5ParkingHaveMostReservation(sqlDate);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get top 5 parking have most reservation" + e.getMessage());
        }
    }

    @Override
    public List<ResponseTop5Revenue> getTop5Revenue(RequestMonthANDYear requestMonthANDYear) {
        try {
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            return reservationMapper.getTop5ParkingHaveHighestRevenue(sqlDate);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get top 5 parking have most reservation" + e.getMessage());
        }
    }

    @Override
    public ReservationInforDTO getInforReservationByLicensesPlate(String licensePlate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<Integer> status = List.of(4, 5);
        Reservation reservation;
        reservation = reservationMapper.findReservationByLicensePlateAndPloId(licensePlate, id, status);
        if (Objects.isNull(reservation)) {
            List<String> licensePlates = licensePlateMapper.getListLicensePlate()
                    .stream().map(LicensePlate::getLicensePlate).collect(Collectors.toList());
            String cleanLicensePlate = licensePlate.replaceAll("[-.]", "");
            for (String licenString : licensePlates) {
                if (cleanLicensePlate.contains(licenString.replaceAll("[-.]", ""))) {
                    reservation = reservationMapper.findReservationByLicensePlateAndPloId(licenString, id, status);
                    break;
                }
            }
            if (Objects.isNull(reservation)) {
                return null;
            }
        }

        if (reservation.getStatusID() == 2) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (currentTime.after(reservation.getEndTime())) {
                reservationMethodMapper.updateStatusReservation(reservation.getReservationID(), 3);
                reservation = reservationMapper.findReservationByLicensePlateAndPloId(licensePlate, id, status);
            }
        }

        Customer customer = customerMapper.getCustomerById(reservation.getCustomerID());
        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        LicensePlate licensePlates = licensePlateMapper.getLicensePlateById(reservation.getLicensePlateID());

        ReservationInforDTO reservationInforDTO = new ReservationInforDTO();
        reservationInforDTO.setCustomerName(customer.getFullName());
        reservationInforDTO.setMethodName(reservationMethod.getMethodName());
        reservationInforDTO.setStatus(reservationStatus.getStatusID());
        reservationInforDTO.setStatusName(reservationStatus.getStatusName());
        reservationInforDTO.setLicensePlate(licensePlates.getLicensePlate());
        reservationInforDTO.setReservationID(reservation.getReservationID());
        reservationInforDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn()) ?
                dateFormat.format(reservation.getCheckIn()) : "");
        reservationInforDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut()) ?
                dateFormat.format(reservation.getCheckOut()) : "");
        reservationInforDTO.setStartTime(Objects.nonNull(reservation.getStartTime()) ?
                dateFormat.format(reservation.getStartTime()) : "");
        reservationInforDTO.setEndTime(Objects.nonNull(reservation.getEndTime()) ?
                dateFormat.format(reservation.getEndTime()) : "");

        return reservationInforDTO;
    }

    @Override
    public List<Top5CustomerDTO> getTop5Customer(RequestMonthANDYear requestMonthANDYear) throws ParseException {
        Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
        java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
        return reservationMapper.getTop5CustomerHaveMostReservation(sqlDate);
    }

    @Override
    public List<ReservationDTO> getReservationHistory() {
        List<ReservationDTO> reservationDTOS = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<Integer> status = List.of(4, 5);

        List<Reservation> responseReservations = reservationMapper.getReservationByStatus(status, id);
        if (responseReservations.isEmpty()) {
            return reservationDTOS;
        }
         //Sắp xếp danh sách responseReservations theo ngày checkIn hoặc checkOut
        responseReservations.sort((r1, r2) -> {
            Date r1Date = null;
            Date r2Date = null;
            if (r1.getCheckIn() != null) {
                r1Date = r1.getCheckIn();
            }else if (r1.getCheckOut() != null) {
                r1Date = r1.getCheckOut();
            }
            if (r2.getCheckIn() != null){
                r2Date = r2.getCheckIn();
            }else if (r2.getCheckOut() != null){
                r2Date = r2.getCheckOut();
            }
            if (r1Date != null && r2Date != null){
                return r2Date.compareTo(r1Date);
            }else {
                return 0;
            }
        });

        for (Reservation reservation : responseReservations) {
            ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
            ReservationDTO reservationDTO = new ReservationDTO();
            PLO plo = parkingLotOwnerMapper.getPloById(reservation.getPloID());
            ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
            reservationDTO.setReservationID(reservation.getReservationID());
            reservationDTO.setAddress(plo.getAddress());
            reservationDTO.setParkingName(plo.getParkingName());
            reservationDTO.setMethodName(reservationMethod.getMethodName());
            reservationDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn()) ?
                    dateFormat.format(reservation.getCheckIn()) : "");
            reservationDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut()) ?
                    dateFormat.format(reservation.getCheckOut()) : "");
            reservationDTO.setStatusID(reservationStatus.getStatusID());
            reservationDTO.setStatusName(reservationStatus.getStatusName());
            reservationDTOS.add(reservationDTO);
        }
        return reservationDTOS;
    }

    @Override
    public ReservationDetailDTO getReservationDetailHistory(int reservationID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Reservation reservation = reservationMapper.getReservationDetailById(reservationID, id);
        if (Objects.isNull(reservation)) {
            return null;
        }
        ReservationDetailDTO reservationDetailDTO = new ReservationDetailDTO();
        PLO plo = parkingLotOwnerMapper.getPloById(reservation.getPloID());
        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
        LicensePlate licensePlate = licensePlateMapper.getLicensePlateById(reservation.getLicensePlateID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        reservationDetailDTO.setFee(reservation.getPrice());
        reservationDetailDTO.setParkingName(plo.getParkingName());
        reservationDetailDTO.setAddress(plo.getAddress());
        reservationDetailDTO.setMethodName(reservationMethod.getMethodName());
        reservationDetailDTO.setLicensePlate(licensePlate.getLicensePlate());
        reservationDetailDTO.setStatusName(reservationStatus.getStatusName());
        reservationDetailDTO.setStartTime(Objects.nonNull(reservation.getStartTime()) ?
                dateFormat.format(reservation.getStartTime()) : "");
        reservationDetailDTO.setEndTime(Objects.nonNull(reservation.getEndTime()) ?
                dateFormat.format(reservation.getEndTime()) : "");
        reservationDetailDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn()) ?
                dateFormat.format(reservation.getCheckIn()) : "");
        reservationDetailDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut()) ?
                dateFormat.format(reservation.getCheckOut()) : "");

        return reservationDetailDTO;
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        try {
            lat1 = Math.toRadians(lat1);
            lon1 = Math.toRadians(lon1);
            lat2 = Math.toRadians(lat2);
            lon2 = Math.toRadians(lon2);

            double R = 6371;

            double dlon = lon2 - lon1;
            double dlat = lat2 - lat1;
            double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c;

            return distance;
        } catch (Exception e) {
            throw new ApiRequestException("Something error with haversine." + e.getMessage());
        }
    }

    @Override
    public List<ResponseFindParkingList> nearestParkingList(RequestFindParkingList findParkingList) {
        try {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            List<ReservationMethod> reservationMethodList = reservationMethodMapper.getMethodByTime(currentTimestamp);
            ReservationMethod reservationMethod = new ReservationMethod();
            if(reservationMethodList.size() >1){
                for (ReservationMethod method:
                        reservationMethodList) {
                    if(method.getMethodID() != 3){
                        reservationMethod = new ReservationMethod(method.getMethodID(), method.getMethodName(), method.getStartTime(),method.getEndTime());
                    }
                }
            }else{
                reservationMethod = reservationMethodList.get(0);
            }

            List<ResponseFindParkingList> responseFindParkingLists = new ArrayList<>();
            List<ResponseCoordinates> coordinates = reservationMapper.getAllCoordinatesPLO();
            if (coordinates == null) {
                throw new ApiRequestException("There is dont have any parking");
            }
            for (ResponseCoordinates responseCoordinates :
                    coordinates) {
                double distance = haversine(findParkingList.getLatitude(), findParkingList.getLongitude(), responseCoordinates.getLatitude(), responseCoordinates.getLongtitude());
                if (distance <= findParkingList.getRadius()) {
                    PLO plo = userMapper.getPLOByPLOID(responseCoordinates.getPloID());
                    if (plo.getParkingStatusID() == 4) {
                        Double price = parkingMethodMapper.getParkingMethodByID(plo.getPloID(), reservationMethod.getMethodID());
                        int slot = plo.getSlot() - plo.getCurrentSlot();
                        List<ResponseMethod> responseMethods = reservationMethodMapper.getMethodByID(plo.getPloID());
                        if (price != null) {
                            responseFindParkingLists.add(new ResponseFindParkingList(plo.getPloID(), plo.getParkingName(), plo.getAddress(), distance, price, currentTimestamp, reservationMethod.getMethodName(), slot, plo.getLongtitude().doubleValue(),plo.getLatitude().doubleValue(),responseMethods));
                        }
                    }
                }
            }
            List<ResponseFindParkingList> sortedList = responseFindParkingLists.stream()
                    .sorted(Comparator.comparingDouble(ResponseFindParkingList::getDistance))
                    .collect(Collectors.toList());
            return sortedList;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get nearest parking list." + e.getMessage());
        }
    }

    @Override
    public List<ResponseFindParkingList> cheapestParkingList(RequestFindParkingList findParkingList) {
        try {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            List<ReservationMethod> reservationMethodList = reservationMethodMapper.getMethodByTime(currentTimestamp);
            ReservationMethod reservationMethod = new ReservationMethod();
            if(reservationMethodList.size() >1){
                for (ReservationMethod method:
                        reservationMethodList) {
                    if(method.getMethodID() != 3){
                        reservationMethod = new ReservationMethod(method.getMethodID(), method.getMethodName(), method.getStartTime(),method.getEndTime());
                    }
                }
            }else {
                reservationMethod = reservationMethodList.get(0);
            }
            List<ResponseFindParkingList> responseFindParkingLists = new ArrayList<>();
            List<ResponseCoordinates> coordinates = reservationMapper.getAllCoordinatesPLO();
            if (coordinates == null) {
                throw new ApiRequestException("There is dont have any parking");
            }
            for (ResponseCoordinates responseCoordinates :
                    coordinates) {
                double distance = haversine(findParkingList.getLatitude(), findParkingList.getLongitude(), responseCoordinates.getLatitude(), responseCoordinates.getLongtitude());
                if (distance <= findParkingList.getRadius()) {
                    PLO plo = userMapper.getPLOByPLOID(responseCoordinates.getPloID());
                    if (plo.getParkingStatusID() == 4) {
                        Double price = parkingMethodMapper.getParkingMethodByID(plo.getPloID(), reservationMethod.getMethodID());
                        int slot = plo.getSlot() - plo.getCurrentSlot();
                        List<ResponseMethod> responseMethods = reservationMethodMapper.getMethodByID(plo.getPloID());
                        if (price != null) {
                            responseFindParkingLists.add(new ResponseFindParkingList(plo.getPloID(), plo.getParkingName(), plo.getAddress(), distance, price, currentTimestamp, reservationMethod.getMethodName(), slot,plo.getLongtitude().doubleValue(),plo.getLatitude().doubleValue(), responseMethods));
                        }
                    }
                }
            }
            List<ResponseFindParkingList> sortedList = responseFindParkingLists.stream()
                    .sorted(Comparator.comparingDouble(ResponseFindParkingList::getPrice))
                    .collect(Collectors.toList());
            return sortedList;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get cheapest parking list." + e.getMessage());
        }
    }

    @Override
    public boolean cancelReservationByID(int reservationID) {
        boolean isCancel = true;
        Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
        if (Objects.isNull(reservation)) {
            isCancel = false;
        } else {
            if (reservation.getStatusID() == 1) {
                Timestamp checkIn = new Timestamp(System.currentTimeMillis());
                Timestamp checkOut = new Timestamp(System.currentTimeMillis());
                reservationMapper.updateCheckInCheckOutIsRatedAndStatusById(reservation.getReservationID(), checkIn, checkOut, 5, 2);
                PLO plo = parkingLotOwnerMapper.getPloById(reservation.getPloID());
                int newCurrentSlot = plo.getCurrentSlot() - 1;
                parkingLotOwnerMapper.updatePloBalanceAndCurrentSlotById(plo.getPloID(), plo.getBalance(), newCurrentSlot);
            } else {
                isCancel = false;
            }

        }

        return isCancel;
    }

    @Override
    public String bookingReservation(BookingReservationDTO bookingReservationDTO) {
        String message = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Customer customer = customerMapper.getCustomerById(id);
        PLO plo = parkingLotOwnerMapper.getPloById(bookingReservationDTO.getPloID());

        LicensePlate licensePlates = licensePlateMapper.
                getLicensePlateByLicensePlate(bookingReservationDTO.getLicensePlate(), id);

        // ** If this license plate have reservation and status reservation ! 4 or !5 -> not allow booking
        List<Reservation> reservations = reservationMapper.getReservationByLicensesPlateId(licensePlates.getLicensePlateID());
        for (Reservation reservation : reservations){
            if (reservation.getStatusID() == 1 || reservation.getStatusID() == 2 || reservation.getStatusID() == 3 ){
                message = Message.NOT_ALLOW_TO_BOOKING;
                return message;
            }
        }
        double methodPrice = parkingMethodMapper.
                getParkingMethodByID(plo.getPloID(), bookingReservationDTO.getMethodID());

        //** if current slot == slot -> return
        if (plo.getCurrentSlot() == plo.getSlot()) {
            message = Message.PARKING_LOT_IS_FULL;
            return message;
        }

        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(bookingReservationDTO.getMethodID());

        if (customer.getWalletBalance() < methodPrice){
            message = Message.NOT_ENOUGH_BALANCE_WALLET;
            return message;
        }
        //** validate startTime & endTime
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp endTime;
        Time calculateTime;
        if (reservationMethod.getMethodID() == 3){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Timestamp futureTimestamp = new Timestamp(calendar.getTimeInMillis());
            String futureTime = futureTimestamp.toString().split(" ")[1].substring(0,8);
            calculateTime = calculateTime(futureTime, plo.getWaitingTime().toString());
            endTime = Timestamp.valueOf(futureTimestamp.toString().split(" ")[0] + " " + calculateTime);
        }else {
            calculateTime = calculateTime(reservationMethod.getEndTime().toString(), plo.getWaitingTime().toString());
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            // Tạo java.sql.Timestamp từ ngày hiện tại
            Timestamp timestamp = new Timestamp(currentDate.getTime());

            // Lấy thời gian từ java.sql.Time và java.sql.Timestamp
            long timeMillis = calculateTime.getTime();
            long timestampMillis = timestamp.getTime();

            // Thực hiện phép cộng thời gian
            long resultMillis = timestampMillis + timeMillis;

            // Kiểm tra xem thời gian sau cộng có vượt qua "00:00:00" hay không
            if (resultMillis < timestampMillis) {
                // Nếu có, thêm một ngày vào kết quả
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                currentDate = calendar.getTime();
            }

            // Tạo định dạng ngày tháng tùy chỉnh
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Tạo kết quả là chuỗi theo định dạng tùy chỉnh
            String resultString = dateFormat.format(currentDate);
            endTime = Timestamp.valueOf(resultString.split(" ")[0] + " " + calculateTime);
        }
        Reservation reservation = new Reservation();
        reservation.setStatusID(1);
        reservation.setPloID(plo.getPloID());
        reservation.setCustomerID(customer.getCustomerID());
        reservation.setPrice(methodPrice);
        reservation.setLicensePlateID(licensePlates.getLicensePlateID());
        reservation.setStartTime(currentTimestamp);
        reservation.setEndTime(endTime);
        reservation.setMethodID(reservationMethod.getMethodID());
        reservationMapper.createReservation(reservation);

        //Minus customer balance and Plus plo balance
        double newCustomerBalance = customer.getWalletBalance() - methodPrice;
        customerMapper.updateBalance(customer.getCustomerID(), newCustomerBalance);

        double newPloBalance = plo.getBalance() + methodPrice;
        int newCurrentSlot = plo.getCurrentSlot() + 1;
        parkingLotOwnerMapper.updatePloBalanceAndCurrentSlotById(plo.getPloID(), newPloBalance, newCurrentSlot);

        message = Message.BOOKING_RESERVATION_SUCCESS;
        return message;
    }

    @Override
    public ResponseScreenReservation getScreenCustomer() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservationSC reservationSC = reservationMapper.getReservationByIsRating(id, 0);
            ResponseScreenReservation screenReservation = new ResponseScreenReservation();
            if (reservationSC == null || reservationSC.getStatusID() == 5) {
                screenReservation.setStatus(1);
                screenReservation.setData(reservationSC);
            } else if (reservationSC.getStatusID() == 1 || reservationSC.getStatusID() == 2 || reservationSC.getStatusID() == 3 || reservationSC.getStatusID() == 4) {
                if (reservationSC.getStatusID() == 1) {
                    screenReservation.setStatus(2);
                } else if (reservationSC.getStatusID() == 2) {
                    screenReservation.setStatus(3);
                } else if (reservationSC.getStatusID() == 3) {
                    screenReservation.setStatus(4);
                } else if (reservationSC.getStatusID() == 4) {
                    screenReservation.setStatus(5);
                }
                PLO plo = userMapper.getPLOByPLOID(reservationSC.getPloID());
                reservationSC.setParkingName(plo.getParkingName());
                reservationSC.setAddress(plo.getAddress());
                reservationSC.setLongitude(plo.getLongtitude().doubleValue());
                reservationSC.setLatitude(plo.getLatitude().doubleValue());
                screenReservation.setData(reservationSC);
            }
            return screenReservation;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get screen customer." + e.getMessage());
        }
    }
    private Time calculateTime(String time1Str, String time2Str){
        try {
            // Parse the input time strings into LocalTime objects
            LocalTime time1 = LocalTime.parse(time1Str, DateTimeFormatter.ofPattern("HH:mm:ss"));
            LocalTime time2 = LocalTime.parse(time2Str, DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Calculate the sum using plusHours, plusMinutes, etc.
            LocalTime sumTime = time1.plusHours(time2.getHour())
                    .plusMinutes(time2.getMinute())
                    .plusSeconds(time2.getSecond());

            return Time.valueOf(sumTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle errors if input times are not valid
        }
    }

    @Override
    public List<ResponseMethodByTime> getListMethodByTime(String ploID) {
        try {
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date parsedDate = dateFormat.parse("2023-10-30 16:35:00");
//            Timestamp currentTimestamp = new Timestamp(parsedDate.getTime());
            ///
            ReservationMethod responseMethod = new ReservationMethod();
            List<ReservationMethod> reservationMethodList = reservationMethodMapper.getMethodByTime(currentTimestamp);
            if(reservationMethodList.size()>1){
                for (ReservationMethod method :
                        reservationMethodList) {
                    if (method.getMethodID() != 3) {
                        responseMethod = new ReservationMethod(method.getMethodID(), method.getMethodName(), method.getStartTime(),method.getEndTime());
                    }
                }
            }else {
                responseMethod = reservationMethodList.get(0);
            }
            List<ParkingMethod> parkingMethods = parkingMethodMapper.getParkingMethodById(ploID);
            List<ResponseMethodByTime> responseMethodByTime = new ArrayList<>();
            List<ReservationMethod> reservationMethodFinal = new ArrayList<>();
            String isSpecial = "";
            //+30m
            Date newDate30m = new Date(currentTimestamp.getTime());
            newDate30m.setTime(newDate30m.getTime() + (30 * 60 * 1000));

            Timestamp Timestamp30m = new Timestamp(newDate30m.getTime());

//
            ReservationMethod responseMethod30m = new ReservationMethod();
            List<ReservationMethod> reservationMethodList30m = reservationMethodMapper.getMethodByTime(Timestamp30m);
            if(reservationMethodList30m.size()>1){
                for (ReservationMethod method :
                        reservationMethodList30m) {
                    if (method.getMethodID() != 3) {
                        responseMethod30m = new ReservationMethod(method.getMethodID(), method.getMethodName(), method.getStartTime(),method.getEndTime());
                    }
                }
            }else {
                responseMethod30m = reservationMethodList30m.get(0);
            }

            //+5m
            Date newDate5m = new Date(currentTimestamp.getTime());
            newDate5m.setTime(newDate5m.getTime() + (5 * 60 * 1000));
            Timestamp Timestamp5m = new Timestamp(newDate5m.getTime());

            //
            ReservationMethod responseMethod5m = new ReservationMethod();
            List<ReservationMethod> reservationMethodList5m = reservationMethodMapper.getMethodByTime(Timestamp5m);
            if(reservationMethodList5m.size() > 1){
                for (ReservationMethod method :
                        reservationMethodList5m) {
                    if (method.getMethodID() != 3) {
                        responseMethod5m = new ReservationMethod(method.getMethodID(), method.getMethodName(), method.getStartTime(),method.getEndTime());
                    }
                }
            }else {
                responseMethod5m = reservationMethodList5m.get(0);
            }

                if (responseMethod.getMethodID() == responseMethod30m.getMethodID()) {
                    reservationMethodFinal.add(new ReservationMethod(responseMethod.getMethodID(), responseMethod.getMethodName(), responseMethod.getStartTime(), responseMethod.getEndTime()));
                } else {
                    isSpecial = responseMethod.getMethodName();
                    reservationMethodFinal.add(new ReservationMethod(responseMethod.getMethodID(), responseMethod.getMethodName(), responseMethod.getStartTime(), responseMethod.getEndTime()));
                    reservationMethodFinal.add(new ReservationMethod(responseMethod30m.getMethodID(), responseMethod30m.getMethodName(), responseMethod30m.getStartTime(), responseMethod30m.getEndTime()));
                    int methodID = responseMethod.getMethodID();
                    if (methodID != responseMethod5m.getMethodID()) {
                        reservationMethodFinal.removeIf(phanTu -> phanTu.getMethodID() == methodID);
                    }
                }
                reservationMethodFinal.add(new ReservationMethod(3, "Qua đêm",
                        new Time(0, 0, 0), // Thời gian bắt đầu (0 giờ, 0 phút, 0 giây)
                        new Time(23, 59, 59)  // Thời gian kết thúc (0 giờ, 0 phút, 0 giây)
                ));
                Set<Integer> uniqueMethodIDs = new HashSet<>();
                reservationMethodFinal.removeIf(phanTu -> !uniqueMethodIDs.add(phanTu.getMethodID()));
                reservationMethodFinal.removeIf(reservationMethod ->
                        parkingMethods.stream().noneMatch(parkingMethod -> parkingMethod.getMethodID() == reservationMethod.getMethodID())
                );
                for (ReservationMethod method :
                        reservationMethodFinal) {
                    boolean special = false;
                    if (method.getMethodName().equals(isSpecial) && !method.getMethodName().equals("Qua đêm")) {
                        special = true;
                    }
                    Double price = parkingMethodMapper.getParkingMethodByID(ploID, method.getMethodID());
                    responseMethodByTime.add(new ResponseMethodByTime(method.getMethodID(), method.getMethodName(), price, special));
                }
            return responseMethodByTime;
            }catch(Exception e){
            throw new ApiRequestException("Failed to get list method by ploID." + e.getMessage());
        }

        }

    @Override
    public BookingDetailDTO bookingDetail(String ploID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        PLO ploEntity = parkingLotOwnerMapper.getPloById(ploID);

        if (Objects.isNull(ploEntity)){
            return null;
        }
        // Get Fee by parking method
        List<ParkingMethod> parkingMethod = parkingMethodMapper.getParkingMethodById(ploID);
        ParkingMethod morningMethod = new ParkingMethod();
        ParkingMethod eveningMethod = new ParkingMethod();
        ParkingMethod overnightMethod = new ParkingMethod();

        List<ReservationMethod> reservationMethods = reservationMethodMapper.getAllReservationMethod();
        for (ReservationMethod reservationMethod : reservationMethods){
            if (reservationMethod.getMethodID() == 1){
                morningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodID() == 2){
                eveningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodID() == 3){
                overnightMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }
        }
        String currentTime = new Timestamp(System.currentTimeMillis()).toString().split(" ")[1];
        List<ReservationBookingMethodDTO> reservationBookingMethodDTOS = new ArrayList<>();
        for (ReservationMethod method : reservationMethods){
            if (method.getMethodID() == 1 &&
            Time.valueOf(currentTime.substring(0, 8)).getTime() > method.getEndTime().getTime()){
                continue;
            }else {
                ReservationBookingMethodDTO reservationBookingMethodDTO = new ReservationBookingMethodDTO();
                reservationBookingMethodDTO.setMethodID(method.getMethodID());
                reservationBookingMethodDTO.setMethodName(method.getMethodName());
                reservationBookingMethodDTOS.add(reservationBookingMethodDTO);
            }
        }

        List<LicensePlate> licensePlates = licensePlateMapper.getListLicensePlateByCustomerID(id);
        List<LicensePlateDTO> licensePlateDTOS = new ArrayList<>();
        for (LicensePlate licensePlate : licensePlates){
            LicensePlateDTO licensePlateDTO = new LicensePlateDTO();
            licensePlateDTO.setLicencePlateID(licensePlate.getLicensePlateID());
            licensePlateDTO.setLicencePlate(licensePlate.getLicensePlate());
            licensePlateDTOS.add(licensePlateDTO);
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        BookingDetailDTO bookingDetailDTO = new BookingDetailDTO();
        bookingDetailDTO.setPloID(ploEntity.getPloID());
        bookingDetailDTO.setAddress(ploEntity.getAddress());
        bookingDetailDTO.setParkingName(ploEntity.getParkingName());
        bookingDetailDTO.setMorningFee(morningMethod != null ? morningMethod.getPrice() : 0);
        bookingDetailDTO.setEveningFee(eveningMethod != null ? eveningMethod.getPrice() : 0);
        bookingDetailDTO.setOvernightFee(overnightMethod != null ? overnightMethod.getPrice() : 0);
        bookingDetailDTO.setWaitingTime(Objects.nonNull(ploEntity.getWaitingTime()) ?
                timeFormat.format(ploEntity.getWaitingTime()) : "");
        bookingDetailDTO.setReservationMethod(reservationBookingMethodDTOS);
        bookingDetailDTO.setCustomerLicensePlate(licensePlateDTOS);
        return bookingDetailDTO;
    }
}
