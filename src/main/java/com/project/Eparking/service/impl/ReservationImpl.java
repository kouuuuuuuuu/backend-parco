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
import com.project.Eparking.domain.request.*;

import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.PushNotificationService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Time;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;
import java.time.*;
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
    private final ReservationStatusMapper reservationStatusMapper;
    private final CustomerMapper customerMapper;
    private final ParkingMethodMapper parkingMethodMapper;
    private final PushNotificationService pushNotificationService;
    private final FirebaseTokenMapper tokenMapper;
    private final ImageMapper imageMapper;
    private final MotorbikeMapper motorbikeMapper;
    private final PriceMethodMapper priceMethodMapper;
    private final ImageGuestMapper imageGuestMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");

    private Timestamp setDate(int year, int month,int day,int hour,int minute,int second){
        LocalDateTime localDateTimeStart = LocalDateTime.of(year, month, day , hour, minute, second, 0);
        return Timestamp.valueOf(localDateTimeStart);
    }

    private Timestamp addTime(Timestamp currentTime, java.sql.Time timeToAdd){
        LocalTime localTime = timeToAdd.toLocalTime();
        LocalDateTime localDateTime = currentTime.toLocalDateTime();
        localDateTime = localDateTime.plusHours(localTime.getHour())
                .plusMinutes(localTime.getMinute())
                .plusSeconds(localTime.getSecond());
        return Timestamp.valueOf(localDateTime);
    }

    public double calculatePrice (int reservationID, Timestamp currentTime) {
        Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
        HashMap<String, Integer> method = new HashMap<>();
        method.put("Morning", 0);
        method.put("Night", 0);
        method.put("Overnight", 0);
        Timestamp startTime = reservation.getEndTime();
        Timestamp endTime = currentTime;
        if(currentTime.before(reservation.getEndTime())){
            return 0;
        }
        LocalDateTime localDateTimeStart1 = startTime.toLocalDateTime();
        int day = localDateTimeStart1.getDayOfMonth();
        int month = localDateTimeStart1.getMonthValue();
        int year = localDateTimeStart1.getYear();

        LocalDateTime localDateTimeEnd1 = endTime.toLocalDateTime();
        int dayEnd = localDateTimeEnd1.getDayOfMonth();
        int monthEnd = localDateTimeEnd1.getMonthValue();
        int yearEnd = localDateTimeEnd1.getYear();

        LocalDateTime localDateTimeStart = LocalDateTime.of(year, month, day , 00, 00, 00, 0);
        LocalDateTime localDateTimeEnd = LocalDateTime.of(yearEnd, monthEnd, dayEnd , 00, 00, 00, 0);
        Timestamp onlyDateStart = Timestamp.valueOf(localDateTimeStart);
        Timestamp onlyDateEnd = Timestamp.valueOf(localDateTimeEnd);
        while (onlyDateStart.before(onlyDateEnd) || onlyDateStart.equals(onlyDateEnd)){

            LocalDateTime current = onlyDateStart.toLocalDateTime();
            int dayCurrent = current.getDayOfMonth();
            int monthCurrent = current.getMonthValue();
            int yearCurrent = current.getYear();

            PLO plo = userMapper.getPLOByPLOID(reservation.getPloID());



            Timestamp method1 = setDate(yearCurrent, monthCurrent, dayCurrent, 06, 00, 00);
            Timestamp method2 = setDate(yearCurrent, monthCurrent, dayCurrent, 17, 00, 00);
            Timestamp method3 = setDate(yearCurrent, monthCurrent, dayCurrent, 23, 00, 00);

            method1 = addTime(method1,plo.getWaitingTime());
            method2 = addTime(method2,plo.getWaitingTime());
            method3 = addTime(method3,plo.getWaitingTime());

            if(method1.after(startTime) && method1.before(endTime)){
                method.put("Morning", method.get("Morning") + 1);
            }
            if(method2.after(startTime) && method2.before(endTime)){
                method.put("Night", method.get("Night") + 1);
            }
            if(method3.after(startTime) && method3.before(endTime)){
                method.put("Overnight", method.get("Overnight") + 1);
            }

            LocalDateTime localDateTime = onlyDateStart.toLocalDateTime();
            LocalDateTime nextDay = localDateTime.plusDays(1);
            onlyDateStart = Timestamp.valueOf(nextDay);
        }
        double total = 0;
        PriceMethod priceMethod = priceMethodMapper.getPriceMethodByReservationID(reservationID);
        total += priceMethod.getMethod1() * method.get("Morning");
        total += priceMethod.getMethod2() * method.get("Night");
        total += priceMethod.getMethod3() * method.get("Overnight");
        return total;
    }
    //region Check-out
    @Override
    @Transactional
    public String checkOutStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id, 2, reservation.getLicensePlate());
            if (responseReservation == null) {
                throw new ApiRequestException("Dont have any reservation with license plate");
            }
            if (responseReservation.getStatusID() == 2) {
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                Customer customerGuest = customerMapper.getGuest();
                PLO plo = userMapper.getPLOByPLOID(responseReservation.getPloID());
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                double totalPrice = calculatePrice(responseReservation.getReservationID(), currentTime);
                if(responseReservation.getCustomerID().equalsIgnoreCase(customerGuest.getCustomerID())){
                    reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 4);
                    reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(), timestamp);
                    int currentSlot = plo.getCurrentSlot() - 1;
                    if (currentSlot < 0) {
                        throw new ApiRequestException("Something error with currentSlot plo");
                    }
                    parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                    reservationMapper.updateIsRatingByReservationID(2,responseReservation.getReservationID());
                    Motorbike motorbike = motorbikeMapper.getLicensePlateByLicensePlateString(reservation.getLicensePlate());
                    if(motorbike.getCustomerID().equalsIgnoreCase(customerGuest.getCustomerID())){
                        motorbikeMapper.deleteLicensePlate(motorbike.getLicensePlateID(),customerGuest.getCustomerID());
                    }
                    priceMethodMapper.updateTotalPrice(responseReservation.getPrice() + totalPrice, responseReservation.getReservationID());
                    return "Check-out for guest successfully";
                }

//                LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 5, 03, 00, 00, 0);
//                Timestamp  currentTime = Timestamp.valueOf(localDateTime);
                Customer customer = customerMapper.getCustomerById(responseReservation.getCustomerID());
                double finalWallet = customer.getWalletBalance() - totalPrice;
                if(customer.getWalletBalance() - totalPrice < 0){
                    throw new ApiRequestException("Tài khoản này cần: "+ Math.abs(finalWallet) + " để thanh toán");
                }
                customerMapper.updateBalance(customer.getCustomerID(),finalWallet);


                double ploWallet = plo.getBalance() + totalPrice;
                parkingLotOwnerMapper.updatePloBalanceById(responseReservation.getPloID(), ploWallet);
                reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 4);

                reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(), timestamp);
                priceMethodMapper.updateTotalPrice(responseReservation.getPrice() + totalPrice, responseReservation.getReservationID());
                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());

                //sendNotiCustomer
                PushNotificationRequest requestCustomer = new PushNotificationRequest();
                List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
                requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR2J-hDEBo9BMtAlzpZFNxltZHP1o2Wh63OfUJwqg1vUJZFO-VHc97q1OLY");
                requestCustomer.setMessage("Bạn đã check out ở bãi xe: "+plo.getParkingName());
                requestCustomer.setTitle("Thông báo tình trạng đặt xe");
                requestCustomer.setTopic("Thông báo tình trạng đặt xe");
                List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(responseReservation.getCustomerID());
                if(firebaseTokensCustomer==null){
                    throw new ApiRequestException("Failed to get firebaseTokens");
                }
                for (FirebaseToken token:
                        firebaseTokensCustomer) {
                    requestCustomer.setToken(token.getDeviceToken());
                    pushNotificationService.sendPushNotificationToToken(requestCustomer);
                }

                response = "Update successfully!";
            } else {
                throw new ApiRequestException("Wrong status");
            }
            return response;
        } catch (Exception e) {
            throw new ApiRequestException("Failed checkOut user." + e.getMessage());
        }
    }
    //endregion
    @Transactional
    @Override
    public String checkInStatusReservation(RequestUpdateStatusReservation reservation) {
        String response = "";
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id, 1, reservation.getLicensePlate());
            if (responseReservation == null) {
                responseReservation = reservationMapper.findReservationByLicensePlate(id, 2, reservation.getLicensePlate());
                if(responseReservation!=null){
                    throw new ApiRequestException("Wrong status");
                }
                throw new ApiRequestException("Visiting Guest!");
            }
            reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 2);
            long epochMilli = Instant.now().toEpochMilli();
            Timestamp timestamp = new Timestamp(epochMilli);
            reservationMethodMapper.updateCheckinReservation(responseReservation.getReservationID(), timestamp);
            PLO plo = userMapper.getPLOByPLOID(responseReservation.getPloID());
            //sendNoti
            PushNotificationRequest requestCustomer = new PushNotificationRequest();
            List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
            requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR2J-hDEBo9BMtAlzpZFNxltZHP1o2Wh63OfUJwqg1vUJZFO-VHc97q1OLY");
            requestCustomer.setMessage("Bạn đã check in ở bãi xe: "+plo.getParkingName());
            requestCustomer.setTitle("Thông báo tình trạng đặt xe");
            requestCustomer.setTopic("Thông báo tình trạng đặt xe");
            List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(responseReservation.getCustomerID());
            if(firebaseTokensCustomer==null){
                throw new ApiRequestException("Failed to get firebaseTokens");
            }
            for (FirebaseToken token:
                    firebaseTokensCustomer) {
                requestCustomer.setToken(token.getDeviceToken());
                pushNotificationService.sendPushNotificationToToken(requestCustomer);
            }
            //
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
            PLO plo = userMapper.getPLOByPLOID(reservation.getPloID());
            //sendNoti
            PushNotificationRequest requestCustomer = new PushNotificationRequest();
            List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
            requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR2J-hDEBo9BMtAlzpZFNxltZHP1o2Wh63OfUJwqg1vUJZFO-VHc97q1OLY");
            requestCustomer.setMessage("Bạn đã check in ở bãi xe: "+plo.getParkingName());
            requestCustomer.setTitle("Thông báo tình trạng đặt xe");
            requestCustomer.setTopic("Thông báo tình trạng đặt xe");
            List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(reservation.getCustomerID());
            if(firebaseTokensCustomer==null){
                throw new ApiRequestException("Failed to get firebaseTokens");
            }
            for (FirebaseToken token:
                    firebaseTokensCustomer) {
                requestCustomer.setToken(token.getDeviceToken());
                pushNotificationService.sendPushNotificationToToken(requestCustomer);
            }
            //
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
            if (statusID == 2) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String id = authentication.getName();
                PLO plo = userMapper.getPLOByPLOID(id);
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//                LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 5, 03, 00, 00, 0);
//
//                // Chuyển đổi LocalDateTime thành Timestamp
//                Timestamp  currentTime = Timestamp.valueOf(localDateTime);
                Customer customerGuest = customerMapper.getGuest();
                Reservation responseReservation = reservationMapper.getReservationByReservationID(reservationID);
                double totalPrice = calculatePrice(reservation.getReservationID(), currentTime);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                if(responseReservation.getCustomerID().equalsIgnoreCase(customerGuest.getCustomerID())){
                    reservationMethodMapper.updateStatusReservation(responseReservation.getReservationID(), 4);
                    reservationMethodMapper.updateCheckoutReservation(responseReservation.getReservationID(), timestamp);
                    int currentSlot = plo.getCurrentSlot() - 1;
                    if (currentSlot < 0) {
                        throw new ApiRequestException("Something error with currentSlot plo");
                    }
                    parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                    reservationMapper.updateIsRatingByReservationID(2,responseReservation.getReservationID());
                    Motorbike motorbike = motorbikeMapper.getLicensePlateById(reservation.getLicensePlateID());
                    if(motorbike.getCustomerID().equalsIgnoreCase(customerGuest.getCustomerID())){
                        motorbikeMapper.deleteLicensePlate(motorbike.getLicensePlateID(),customerGuest.getCustomerID());
                    }
                    priceMethodMapper.updateTotalPrice(responseReservation.getPrice() + totalPrice, responseReservation.getReservationID());
                    return "Check-out for guest successfully";
                }

                Customer customer = customerMapper.getCustomerById(reservation.getCustomerID());
                double finalWallet = customer.getWalletBalance() - totalPrice;
                if(customer.getWalletBalance() - totalPrice < 0){
                    throw new ApiRequestException("Tài khoản này cần: "+ Math.abs(finalWallet) + " để thanh toán");
                }
                customerMapper.updateBalance(customer.getCustomerID(),finalWallet);

                double ploWallet = plo.getBalance() + totalPrice;
                parkingLotOwnerMapper.updatePloBalanceById(reservation.getPloID(), ploWallet);
                priceMethodMapper.updateTotalPrice(reservation.getPrice() + totalPrice, reservation.getReservationID());
                reservationMethodMapper.updateStatusReservation(reservationID, 4);
                reservationMethodMapper.updateCheckoutReservation(reservationID, timestamp);

                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                //sendNoti
                PushNotificationRequest requestCustomer = new PushNotificationRequest();
                List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
                requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR2J-hDEBo9BMtAlzpZFNxltZHP1o2Wh63OfUJwqg1vUJZFO-VHc97q1OLY");
                requestCustomer.setMessage("Bạn đã check out ở bãi xe: "+plo.getParkingName());
                requestCustomer.setTitle("Thông báo tình trạng đặt xe");
                requestCustomer.setTopic("Thông báo tình trạng đặt xe");
                List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(reservation.getCustomerID());
                if(firebaseTokensCustomer==null){
                    throw new ApiRequestException("Failed to get firebaseTokens");
                }
                for (FirebaseToken token:
                        firebaseTokensCustomer) {
                    requestCustomer.setToken(token.getDeviceToken());
                    pushNotificationService.sendPushNotificationToToken(requestCustomer);
                }
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
            throw new ApiRequestException("Failed to get top 5 parking have highest revenue" + e.getMessage());
        }
    }

    @Override
    public ReservationInforDTO getInforReservationByLicensesPlate(String licensePlate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Customer guest = customerMapper.getGuest();
        List<Integer> status = List.of(4, 5);
        Reservation reservation;
        reservation = reservationMapper.findReservationByLicensePlateAndPloId(licensePlate, id, status);
        if (Objects.isNull(reservation)) {
            List<String> licensePlates = motorbikeMapper.getListLicensePlate()
                    .stream().map(Motorbike::getLicensePlate).collect(Collectors.toList());
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
        if(reservation.getCustomerID().equalsIgnoreCase(guest.getCustomerID())){
            ResponseGuest responseGuest = new ResponseGuest();

            responseGuest.setReservationID(responseGuest.getReservationID());
            responseGuest.setType("GUEST");
            responseGuest.setCustomerID(reservation.getCustomerID());
            responseGuest.setPloID(reservation.getPloID());
            responseGuest.setStatusID(reservation.getStatusID());
            ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
            responseGuest.setStatusName(reservationStatus.getStatusName());
            responseGuest.setLicensePlate(licensePlate);
            responseGuest.setCheckIn(Objects.nonNull(reservation.getCheckIn()) ?
                    dateFormat.format(reservation.getCheckIn()) : "");
            responseGuest.setEndTime(Objects.nonNull(reservation.getEndTime()) ?
                    dateFormat.format(reservation.getEndTime()) : "");
            responseGuest.setStartTime(Objects.nonNull(reservation.getStartTime()) ?
                    dateFormat.format(reservation.getStartTime()) : "");
            responseGuest.setCheckOut(Objects.nonNull(reservation.getCheckOut()) ?
                    dateFormat.format(reservation.getCheckOut()) : "");
            ImageGuest imageGuest = imageGuestMapper.getImageGuestByReservationID(reservation.getReservationID());
            responseGuest.setImage(imageGuest.getImageLink());
            responseGuest.setMethodID(reservation.getMethodID());
            ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
            responseGuest.setMethodName(reservationMethod.getMethodName());
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            double total = calculatePrice(reservation.getReservationID(), currentTimestamp);
            responseGuest.setPriceMethod(reservation.getPrice());
            responseGuest.setTotal(reservation.getPrice() + total);
            responseGuest.setReservationID(reservation.getReservationID());
//            return ResponseEntity.ok(responseGuest);
        }

        Customer customer = customerMapper.getCustomerById(reservation.getCustomerID());
        ReservationMethod reservationMethod = reservationMethodMapper.getReservationMethodById(reservation.getMethodID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        Motorbike motorbike = motorbikeMapper.getLicensePlateById(reservation.getLicensePlateID());

        ReservationInforDTO reservationInforDTO = new ReservationInforDTO();
        reservationInforDTO.setCustomerID(customer.getCustomerID());
        reservationInforDTO.setCustomerName(customer.getFullName());
        reservationInforDTO.setMethodName(reservationMethod.getMethodName());
        reservationInforDTO.setStatus(reservationStatus.getStatusID());
        reservationInforDTO.setStatusName(reservationStatus.getStatusName());
        reservationInforDTO.setLicensePlate(motorbike.getLicensePlate());
        reservationInforDTO.setMotorbikeName(motorbike.getMotorbikeName());
        reservationInforDTO.setMotorbikeColor(motorbike.getMotorbikeColor());
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
            }
            else if (r1Date != null) {
                return -1; // r1Date is considered smaller
            } else if (r2Date != null) {
                return 1; // r2Date is considered smaller
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
            PriceMethod priceMethod = priceMethodMapper.getTotalPriceOfReservation(reservation.getReservationID());
            if (Objects.nonNull(priceMethod)) {
                reservationDTO.setTotalPrice(priceMethod.getTotal());
            }
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
        Motorbike motorbike = motorbikeMapper.getLicensePlateById(reservation.getLicensePlateID());
        ReservationStatus reservationStatus = reservationStatusMapper.getReservationStatusByID(reservation.getStatusID());
        reservationDetailDTO.setFee(reservation.getPrice());
        reservationDetailDTO.setParkingName(plo.getParkingName());
        reservationDetailDTO.setAddress(plo.getAddress());
        reservationDetailDTO.setMethodName(reservationMethod.getMethodName());
        reservationDetailDTO.setLicensePlate(motorbike.getLicensePlate());
        reservationDetailDTO.setStatusName(reservationStatus.getStatusName());
        reservationDetailDTO.setStartTime(Objects.nonNull(reservation.getStartTime()) ?
                dateFormat.format(reservation.getStartTime()) : "");
        reservationDetailDTO.setEndTime(Objects.nonNull(reservation.getEndTime()) ?
                dateFormat.format(reservation.getEndTime()) : "");
        reservationDetailDTO.setCheckIn(Objects.nonNull(reservation.getCheckIn()) ?
                dateFormat.format(reservation.getCheckIn()) : "");
        reservationDetailDTO.setCheckOut(Objects.nonNull(reservation.getCheckOut()) ?
                dateFormat.format(reservation.getCheckOut()) : "");
        PriceMethod priceMethod = priceMethodMapper.getTotalPriceOfReservation(reservation.getReservationID());
        if (Objects.nonNull(priceMethod)) {
            reservationDetailDTO.setTotalPrice(priceMethod.getTotal());
        }
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
            Time oneH= Time.valueOf("01:00:00");
            currentTimestamp = addTime(currentTimestamp,oneH);
            ReservationMethod reservationMethod = reservationMethodMapper.getMethodByTimeReturn1(currentTimestamp);
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
                        if (price != null && slot > 0) {
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
            Time oneH= Time.valueOf("01:00:00");
            currentTimestamp = addTime(currentTimestamp,oneH);
            ReservationMethod reservationMethod = reservationMethodMapper.getMethodByTimeReturn1(currentTimestamp);
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
                        if (price != null && slot > 0) {
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
                priceMethodMapper.updateTotalPrice(reservation.getPrice(),reservation.getReservationID());
                //send noti
                PushNotificationRequest request = new PushNotificationRequest();
                request.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR0Cp0mqjcD5-DCi9DvSSomsni8_gA-tg14f2GskVlpIYReh-tagSlOrb-4");
                Customer customer = customerMapper.getCustomerById(reservation.getCustomerID());
                request.setMessage("Khách hàng: "+customer.getFullName()+" đã hủy đặt chỗ");
                request.setTitle("Thông báo trạng thái bãi xe");
                request.setTopic("Thông báo trạng thái bãi xe");
                List<FirebaseToken> firebaseTokens = tokenMapper.getTokenByID(plo.getPloID());
                if(firebaseTokens==null){
                    throw new ApiRequestException("Failed to get firebaseTokens");
                }
                for (FirebaseToken token:
                        firebaseTokens) {
                    request.setToken(token.getDeviceToken());
                    pushNotificationService.sendPushNotificationToToken(request);
                }
            } else {
                isCancel = false;
            }

        }

        return isCancel;
    }
    @Transactional
    @Override
    public String bookingReservation(BookingReservationDTO bookingReservationDTO) {
        String message = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Customer customer = customerMapper.getCustomerById(id);
        PLO plo = parkingLotOwnerMapper.getPloById(bookingReservationDTO.getPloID());

        Motorbike licensePlates = motorbikeMapper.
                getLicensePlateByLicensePlate(bookingReservationDTO.getMotorbikeID(), id);

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

        // Update new logic booking to table priceMethodReservation
        List<ParkingMethod> parkingMethods = parkingMethodMapper.getParkingMethodById(plo.getPloID());
        PriceMethod priceMethod = new PriceMethod();
        for (ParkingMethod parkingMethod : parkingMethods){
            if (parkingMethod.getMethodID() == 1){
                priceMethod.setMethod1(parkingMethod.getPrice());
            }
            if (parkingMethod.getMethodID() == 2){
                priceMethod.setMethod2(parkingMethod.getPrice());
            }
            if (parkingMethod.getMethodID() == 3){
                priceMethod.setMethod3(parkingMethod.getPrice());
            }
        }
        if (priceMethod.getMethod1() == 0){
            priceMethod.setMethod1(3000);
        }
        if (priceMethod.getMethod2() == 0){
            priceMethod.setMethod2(4000);
        }
        if (priceMethod.getMethod3() == 0){
            priceMethod.setMethod3(7000);
        }
        priceMethod.setReservationID(reservation.getReservationID());
        priceMethodMapper.create(priceMethod);

        message = Message.BOOKING_RESERVATION_SUCCESS;

        //sentnoti PLO
        PushNotificationRequest request = new PushNotificationRequest();
        request.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR0Cp0mqjcD5-DCi9DvSSomsni8_gA-tg14f2GskVlpIYReh-tagSlOrb-4");
        request.setMessage("Khách hàng: "+customer.getFullName()+" đã đặt chỗ");
        request.setTitle("Thông báo trạng thái bãi xe");
        request.setTopic("Thông báo trạng thái bãi xe");
        List<FirebaseToken> firebaseTokens = tokenMapper.getTokenByID(plo.getPloID());
        if(firebaseTokens==null){
            throw new ApiRequestException("Failed to get firebaseTokens");
        }
        for (FirebaseToken token:
                firebaseTokens) {
            request.setToken(token.getDeviceToken());
            pushNotificationService.sendPushNotificationToToken(request);
        }
//        Notifications notifications = new Notifications();
//        notifications.setSender_type("CUSTOMER");
//        notifications.setSender_id(id);
//        notifications.setRecipient_type("PLO");
//        notifications.setRecipient_id(bookingReservationDTO.getPloID());
//        notifications.setCreated_at(currentTimestamp);
//        notifications.setContent("Khách hàng: "+id+" đã đặt chỗ");
//        userMapper.insertNotification(notifications);

        //sendNotiCustomer
        List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
        PushNotificationRequest requestCustomer = new PushNotificationRequest();
        requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR0Cp0mqjcD5-DCi9DvSSomsni8_gA-tg14f2GskVlpIYReh-tagSlOrb-4");
        requestCustomer.setMessage("Bạn đã đặt chỗ ở bãi xe: "+plo.getParkingName());
        requestCustomer.setTitle("Thông báo tình trạng đặt xe");
        requestCustomer.setTopic("Thông báo tình trạng đặt xe");
        List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(customer.getCustomerID());
        if(firebaseTokensCustomer==null){
            throw new ApiRequestException("Failed to get firebaseTokens");
        }
        for (FirebaseToken token:
                firebaseTokensCustomer) {
            requestCustomer.setToken(token.getDeviceToken());
            pushNotificationService.sendPushNotificationToToken(requestCustomer);
        }
        Notifications notifications = new Notifications();
        notifications.setSender_type("PLO");
        notifications.setSender_id(plo.getPloID());
        notifications.setRecipient_type("CUSTOMER");
        notifications.setRecipient_id(id);
        notifications.setCreated_at(currentTimestamp);
        notifications.setContent("Bạn đã đặt chỗ ở bãi xe: "+plo.getParkingName());
        notifications.setImageLink("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR0Cp0mqjcD5-DCi9DvSSomsni8_gA-tg14f2GskVlpIYReh-tagSlOrb-4");
        userMapper.insertNotification(notifications);
        return message;
    }

    @Override
    public ResponseScreenReservation getScreenCustomer() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseReservationSC reservationSC = reservationMapper.getReservationByIsRating(id, 0);
            ResponseScreenCustomer responseScreenCustomer = new ResponseScreenCustomer();
            ResponseScreenReservation screenReservation = new ResponseScreenReservation();
            if (reservationSC == null || reservationSC.getStatusID() == 5) {
                screenReservation.setStatus(1);
                screenReservation.setData(responseScreenCustomer);
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
                responseScreenCustomer.setParkingName(plo.getParkingName());
                responseScreenCustomer.setAddress(plo.getAddress());
                responseScreenCustomer.setLongitude(plo.getLongtitude().doubleValue());
                responseScreenCustomer.setLatitude(plo.getLatitude().doubleValue());
                responseScreenCustomer.setStartTime(Objects.nonNull(reservationSC.getStartTime()) ?
                        dateFormat.format(reservationSC.getStartTime()) : "");
                responseScreenCustomer.setEndTime(Objects.nonNull(reservationSC.getEndTime()) ?
                        dateFormat.format(reservationSC.getEndTime()) : "");
                responseScreenCustomer.setWaitingTime(plo.getWaitingTime());
                responseScreenCustomer.setCancelBookingTime(plo.getCancelBookingTime());
                responseScreenCustomer.setPloID(plo.getPloID());
                responseScreenCustomer.setStatusName(reservationSC.getStatusName());
                responseScreenCustomer.setStatusID(reservationSC.getStatusID());
                responseScreenCustomer.setPrice(reservationSC.getPrice());
                responseScreenCustomer.setMethodName(reservationSC.getMethodName());
                responseScreenCustomer.setLicensePlate(reservationSC.getLicensePlate());
                responseScreenCustomer.setReservationID(reservationSC.getReservationID());
                screenReservation.setData(responseScreenCustomer);
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

        List<Motorbike> motorbikes = motorbikeMapper.getListLicensePlateByCustomerID(id);
        List<MotorbikeDTO> motorbikeDTOS = new ArrayList<>();
        for (Motorbike motorbike : motorbikes){
            MotorbikeDTO motorbikeDTO = new MotorbikeDTO();
            motorbikeDTO.setMotorbikeID(motorbike.getLicensePlateID());
            motorbikeDTO.setLicensePlate(motorbike.getLicensePlate());
            motorbikeDTO.setMotorbikeColor(motorbike.getMotorbikeColor());
            motorbikeDTO.setMotorbikeName(motorbike.getMotorbikeName());
            motorbikeDTOS.add(motorbikeDTO);
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
        bookingDetailDTO.setCustomerLicensePlate(motorbikeDTOS);
        return bookingDetailDTO;
    }

    @Override
    public String checkOutWithoutCheckCondition(int reservationID) {
        try{
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            int statusID = reservation.getStatusID();
            if (statusID == 2) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String id = authentication.getName();
                PLO plo = userMapper.getPLOByPLOID(id);
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//                LocalDateTime localDateTime = LocalDateTime.of(2023, 12, 5, 03, 00, 00, 0);
//
//                // Chuyển đổi LocalDateTime thành Timestamp
//                Timestamp  currentTime = Timestamp.valueOf(localDateTime);
                double totalPrice = calculatePrice(reservation.getReservationID(), currentTime);
                priceMethodMapper.updateTotalPrice(reservation.getPrice() + totalPrice, reservation.getReservationID());
                reservationMethodMapper.updateStatusReservation(reservationID, 4);
                long epochMilli = Instant.now().toEpochMilli();
                Timestamp timestamp = new Timestamp(epochMilli);
                reservationMethodMapper.updateCheckoutReservation(reservationID, timestamp);

                int currentSlot = plo.getCurrentSlot() - 1;
                if (currentSlot < 0) {
                    throw new ApiRequestException("Something error with currentSlot plo");
                }
                parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                //sendNoti
                PushNotificationRequest requestCustomer = new PushNotificationRequest();
                List<Image> images = imageMapper.getImageByPloId(plo.getPloID());
                requestCustomer.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR2J-hDEBo9BMtAlzpZFNxltZHP1o2Wh63OfUJwqg1vUJZFO-VHc97q1OLY");
                requestCustomer.setMessage("Bạn đã check out ở bãi xe: "+plo.getParkingName());
                requestCustomer.setTitle("Thông báo tình trạng đặt xe");
                requestCustomer.setTopic("Thông báo tình trạng đặt xe");
                List<FirebaseToken> firebaseTokensCustomer = tokenMapper.getTokenByID(reservation.getCustomerID());
                if(firebaseTokensCustomer==null){
                    throw new ApiRequestException("Failed to get firebaseTokens");
                }
                for (FirebaseToken token:
                        firebaseTokensCustomer) {
                    requestCustomer.setToken(token.getDeviceToken());
                    pushNotificationService.sendPushNotificationToToken(requestCustomer);
                }
                return "Update successfully!";
            }else {
                throw new ApiRequestException("Wrong status");
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to checkout reservation." + e.getMessage());
        }
    }

    @Override
    public ResponseMethodByTimePLOID getMethodByTime(String ploID) {
        try{
            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            Time oneH= Time.valueOf("01:00:00");
            currentTimestamp = addTime(currentTimestamp,oneH);
            ReservationMethod reservationMethod = reservationMethodMapper.getMethodByTimeReturn1(currentTimestamp);
            ParkingMethod method = parkingMethodMapper.getParkingMethodByIdMethod(ploID, reservationMethod.getMethodID());
            if(method == null){
                throw new ApiRequestException("Wrong method or method is correct!");
            }
            ResponseMethodByTimePLOID responseMethodByTimePLOID = new ResponseMethodByTimePLOID();
            responseMethodByTimePLOID.setMethodID(method.getMethodID());
            responseMethodByTimePLOID.setPrice(method.getPrice());
            responseMethodByTimePLOID.setMethodName(reservationMethod.getMethodName());
            return responseMethodByTimePLOID;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get method ploID by Time." + e.getMessage());
        }
    }
    private Timestamp calculateEndtime(Timestamp currentTime, Time endTime, Time waitingTime, int method) {
        LocalDateTime currentDateTime = currentTime.toLocalDateTime();
        LocalTime endLocalTime = endTime.toLocalTime();
        LocalTime waitingLocalTime = waitingTime.toLocalTime();
        LocalDateTime result;
        if(method == 1 || method == 2) {
            LocalDate currentDate = currentDateTime.toLocalDate();
            result = LocalDateTime.of(currentDate, endLocalTime);
        } else if(method == 3) {
            LocalDate currentDate = currentDateTime.toLocalDate().plusDays(1);
            result = LocalDateTime.of(currentDate, endLocalTime);
        } else {
            throw new IllegalArgumentException("Invalid method");
        }

        Duration waitingDuration = Duration.ofHours(waitingLocalTime.getHour())
                .plusMinutes(waitingLocalTime.getMinute())
                .plusSeconds(waitingLocalTime.getSecond());
        result = result.plus(waitingDuration);
        Timestamp timestamp = Timestamp.valueOf(result);
        return timestamp;
    }
    @Transactional
    @Override
    public String bookingByGuest(GuestBooking guestBooking) {
        try {
            if(guestBooking.getImage() == null || guestBooking.getLicensePlate() == null){
                throw new ApiRequestException("Fields is invalid");
            }
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            PLO plo = userMapper.getPLOByPLOID(id);
            if(plo.getSlot() - plo.getCurrentSlot() <= 0){
                throw new ApiRequestException("Current slot is invalid");
            }
            ResponseReservation responseReservation = reservationMapper.findReservationByLicensePlate(id,1,guestBooking.getLicensePlate());
            if(responseReservation != null){
                throw new ApiRequestException("Invalid status with license plate");
            }
            responseReservation = reservationMapper.findReservationByLicensePlate(id,2,guestBooking.getLicensePlate());
            if(responseReservation != null){
                throw new ApiRequestException("Invalid status with license plate");
            }

            Date currentDate = new Date();
            Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
            ReservationMethod reservationMethod = reservationMethodMapper.getMethodByTimeReturn1(currentTimestamp);
            ParkingMethod method = parkingMethodMapper.getParkingMethodByIdMethod(id, reservationMethod.getMethodID());
            if(method == null){
                throw new ApiRequestException("Invalid method in parking");
            }
            Motorbike motorbike = motorbikeMapper.getLicensePlateByLicensePlateString(guestBooking.getLicensePlate());
            if(motorbike == null){
                motorbikeMapper.insertForGuest(guestBooking.getLicensePlate(),"guest","guest");
                motorbike = motorbikeMapper.getLicensePlateByLicensePlateString(guestBooking.getLicensePlate());
            }
            Timestamp endTime = calculateEndtime(currentTimestamp,reservationMethod.getEndTime(),plo.getWaitingTime(),reservationMethod.getMethodID());
            Customer customerGuest = customerMapper.getGuest();

            Reservation reservation = new Reservation();
            reservation.setStatusID(2);
            reservation.setPloID(plo.getPloID());
            reservation.setCustomerID(customerGuest.getCustomerID());
            reservation.setPrice(method.getPrice());
            reservation.setLicensePlateID(motorbike.getLicensePlateID());
            reservation.setStartTime(currentTimestamp);
            reservation.setEndTime(endTime);
            reservation.setMethodID(reservationMethod.getMethodID());
            reservationMapper.createReservation(reservation);

            List<ParkingMethod> parkingMethods = parkingMethodMapper.getParkingMethodById(plo.getPloID());
            PriceMethod priceMethod = new PriceMethod();
            for (ParkingMethod parkingMethod : parkingMethods){
                if (parkingMethod.getMethodID() == 1){
                    priceMethod.setMethod1(parkingMethod.getPrice());
                }
                if (parkingMethod.getMethodID() == 2){
                    priceMethod.setMethod2(parkingMethod.getPrice());
                }
                if (parkingMethod.getMethodID() == 3){
                    priceMethod.setMethod3(parkingMethod.getPrice());
                }
            }
            if (priceMethod.getMethod1() == 0){
                priceMethod.setMethod1(3000);
            }
            if (priceMethod.getMethod2() == 0){
                priceMethod.setMethod2(4000);
            }
            if (priceMethod.getMethod3() == 0){
                priceMethod.setMethod3(7000);
            }
            priceMethod.setReservationID(reservation.getReservationID());
            priceMethodMapper.create(priceMethod);
            int newCurrentSlot = plo.getCurrentSlot() + 1;
            parkingLotOwnerMapper.updatePloBalanceAndCurrentSlotById(plo.getPloID(), plo.getBalance(), newCurrentSlot);
            imageGuestMapper.insertImageGuest(reservation.getReservationID(),guestBooking.getImage());
            reservationMapper.updateCheckInByReservationID(currentTimestamp,reservation.getReservationID());
            return "Booking successfully by guest!";
        }catch (Exception e){
            throw new ApiRequestException("Can create booking for guest." + e.getMessage());
        }
    }
}
