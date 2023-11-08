package com.project.Eparking.service.impl;

import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.dto.*;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.dto.CustomerWalletDTO;
import com.project.Eparking.domain.request.RequestChangePassword;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerTransaction;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.PushNotificationService;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final LicensePlateMapper licensePlateMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PaymentService paymentService;
    private final CustomerTransactionMapper transactionMapper;
    private final ImageMapper imageMapper;
    private final ParkingLotOwnerMapper parkingLotOwnerMapper;
    private final ParkingMethodMapper parkingMethodMapper;
    private final ReservationMethodMapper reservationMethodMapper;
    private final FirebaseTokenMapper firebaseTokenMapper;
    private final PushNotificationService pushNotificationService;
    private final ReservationMapper reservationMapper;
    private final ParkingMapper parkingMapper;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    public Page<CustomerDTO> getListCustomer(int pageNum, int pageSize) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list customer from database
        List<Customer> listCustomerEntity = customerMapper.getListCustomer(pageNumOffset, pageSize);
        if (listCustomerEntity.isEmpty()){
            return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, 0);
        }
        //2. Mapping customer data to customer DTO;
        for (Customer c : listCustomerEntity){
            List <FirebaseToken> firebaseToken = firebaseTokenMapper.getFirebaseTokenByCustomerID(c.getCustomerID());
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(c.getCustomerID());
            customerDTO.setPhoneNumber(c.getPhoneNumber());
            customerDTO.setFullName(c.getFullName());
            customerDTO.setStatus((Objects.nonNull(firebaseToken) && !firebaseToken.isEmpty()) ?
                    "Online" : "Offline");
            String registrationDate = dateFormat.format(c.getRegistrationDate());
            customerDTO.setRegistrationDate(registrationDate);

            //2.1. Get licensePlate total number by list customerId;
            List<LicensePlate> licensePlateList = licensePlateMapper.getListLicensePlateByCustomerID(c.getCustomerID());
            customerDTO.setTotalNumber(licensePlateList.size());
            customerDTOList.add(customerDTO);
        }

        int totalRecords = this.countRecords("");

        return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, totalRecords);
    }

    @Override
    public Page<CustomerDTO> getListCustomerByName(String name, int pageNum, int pageSize) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list customer from database
        String searchKeyword =name.trim();
        List<Customer> listCustomers = customerMapper.getListCustomerByName(searchKeyword, pageNumOffset, pageSize);
        if(listCustomers.isEmpty()){
            return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, 0);
        }

        //2. Mapping to DTO data
        for (Customer c : listCustomers){
            List<FirebaseToken> firebaseToken = firebaseTokenMapper.getFirebaseTokenByCustomerID(c.getCustomerID());
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(c.getCustomerID());
            customerDTO.setPhoneNumber(c.getPhoneNumber());
            customerDTO.setFullName(c.getFullName());
            customerDTO.setStatus((Objects.nonNull(firebaseToken) && !firebaseToken.isEmpty()) ?
                    "Online" : "Offline");
            String registrationDate = dateFormat.format(c.getRegistrationDate());
            customerDTO.setRegistrationDate(registrationDate);

            //2.1. Get licensePlate total number by list customerId;
            List<LicensePlate> licensePlateList = licensePlateMapper.getListLicensePlateByCustomerID(c.getCustomerID());
            customerDTO.setTotalNumber(licensePlateList.size());
            customerDTOList.add(customerDTO);
        }

        int totalRecords = this.countRecords(searchKeyword);

        return new Page<CustomerDTO>(customerDTOList, pageNum, pageSize, totalRecords);
    }

    private Integer countRecords(String keyword){
        return customerMapper.countRecords(keyword);
    }

    @Override
    public ResponseCustomer getResponseCustomerByCustomerID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            return userMapper.getResponseCustomerByCustomerID(id);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get customer profile" +e.getMessage());
        }
    }

    @Override
    public String updateCustomerProfile(RequestCustomerUpdateProfile profile) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            customerMapper.updateCustomerProfile(profile,id);
            return "Update profile successfully";
        }catch (Exception e){
            throw new ApiRequestException("Failed to update customer profile" +e.getMessage());
        }
    }

    @Override
    public List<String> updatePassswordCustomer(RequestChangePasswordUser customerParam) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<String> response = new ArrayList<>();
            boolean check = true;
            Customer customer = userMapper.getCustomerByCustomerID(id);
            if(!passwordEncoder.matches(customerParam.getCurrentPassword(),customer.getPassword())){
                check = false;
                response.add("The current password is wrong!");
            }
            if (!customerParam.getNewPassword().equals(customerParam.getReNewPassword())) {
                check = false;
                response.add("The new password is not match");
            }
            if(check){
                customerMapper.updatePasswordCustomer(passwordEncoder.encode(customerParam.getNewPassword()),id);
                response.add("Update new password successfully");
            }
            return response;
        }catch (Exception e){
            throw new ApiRequestException("Failed to update password customer" +e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> createPaymentCustomer(HttpServletRequest req,RequestCustomerTransaction transaction) {
       try{
           Payment payment = new Payment();
           payment.setAmountParam(String.valueOf(transaction.getAmount()));
           return paymentService.createPaymentCustomer(req,payment);
       }catch (Exception e){
           throw new ApiRequestException("Failed to create payment customer" +e.getMessage());
       }
    }

    @Override
    public ResponseWalletScreen responseWalletScreen() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            ResponseWalletScreen responseWalletScreen = new ResponseWalletScreen();
            responseWalletScreen.setWallet_balance(userMapper.getCustomerByCustomerID(id).getWalletBalance());
            responseWalletScreen.setHistoryBalanceCustomerList(transactionMapper.getListTransactionCustomer(id));
            return responseWalletScreen;
        }catch (Exception e){
            throw new ApiRequestException("Failed to response wallet screen" +e.getMessage());
        }
    }

    @Override
    public List<WeekData> countRecordsByWeekCustomer(RequestMonthANDYear requestMonthANDYear) {
        try {
            Date inputDate = new SimpleDateFormat("yyyy-MM").parse(requestMonthANDYear.getMonthAndYear());
            java.sql.Date sqlDate = new java.sql.Date(inputDate.getTime());
            Response4week week = customerMapper.countRecordsByWeekCustomer(sqlDate);
            List<WeekData> result = new ArrayList<>();
            result.add(new WeekData("Tuần 1", week.getWeek1()));
            result.add(new WeekData("Tuần 2", week.getWeek2()));
            result.add(new WeekData("Tuần 3", week.getWeek3()));
            result.add(new WeekData("Tuần 4", week.getWeek4()));
            return result;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get chart week for customer" +e.getMessage());
        }
    }

//    @Override
//    public CustomerWalletDTO getCustomerBalance() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String id = authentication.getName();
//        Customer customer = customerMapper.getCustomerBalance(id);
//        CustomerWalletDTO customerWalletDTO = new CustomerWalletDTO();
////        customerWalletDTO.setCustomerID(customer.getCustomerID());
//        customerWalletDTO.setWallet_balance(customer.getWalletBalance());
//        return  customerWalletDTO;
//    }

    @Override
    public CustomerWalletDTO getCustomerBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Customer customer = customerMapper.getCustomerBalance(id);
        CustomerWalletDTO customerWalletDTO = new CustomerWalletDTO();
//        customerWalletDTO.setCustomerID(customer.getCustomerID());
        customerWalletDTO.setWallet_balance(customer.getWalletBalance());
        return  customerWalletDTO;
    }

    @Override
    public PloDetailForCustomerDTO getPloDetailForCustomer(String ploID) {
        //1. Get Image by ploId
        List<Image> images = imageMapper.getImageByPloId(ploID);

        //3. Get Plo by ploId;
        PLO ploEntity = parkingLotOwnerMapper.getPloById(ploID);
        if (Objects.isNull(ploEntity)){
            return null;
        }

        //4. Get Fee by parking method
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
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        PloDetailForCustomerDTO parkingLotOwnerDTO = new PloDetailForCustomerDTO();
        parkingLotOwnerDTO.setParkingName(ploEntity.getParkingName());
        parkingLotOwnerDTO.setAddress(ploEntity.getAddress());

        parkingLotOwnerDTO.setMorningFee(morningMethod != null ? morningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setEveningFee(eveningMethod != null ? eveningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setOvernightFee(overnightMethod != null ? overnightMethod.getPrice() : 0);
        parkingLotOwnerDTO.setStar(ploEntity.getStar());
        parkingLotOwnerDTO.setCurrentSlot(ploEntity.getSlot() - ploEntity.getCurrentSlot());
        parkingLotOwnerDTO.setImages(imageDTOS);
        parkingLotOwnerDTO.setWaitingTime(Objects.nonNull(ploEntity.getWaitingTime()) ?
                timeFormat.format(ploEntity.getWaitingTime()) : "");
        return parkingLotOwnerDTO;
    }

    @Override
    public void notificationBefore15mCancelBooking(int reservationID) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<FirebaseToken> firebaseTokens = firebaseTokenMapper.getFirebaseTokenByCustomerID(id);
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            PushNotificationRequest request = new PushNotificationRequest();
            request.setTitle("Thông báo trạng thái của lần đặt xe");
            request.setTopic("Thông báo trạng thái của lần đặt xe");
            request.setMessage("Còn 15 phút trước khi lần đặt này bị hủy");
            List<Image> images = imageMapper.getImageListByPLOID(reservation.getPloID());
            Image image = images.get(0);
            request.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR3x6Nj8bIgirE0qXYMzXlHHe2AihooslWKegmc9iLVBO7ihisPqJm4pM3k");
            for (FirebaseToken token :
                    firebaseTokens) {
                request.setToken(token.getDeviceToken());
                pushNotificationService.sendPushNotificationToToken(request);
            }
        }catch (Exception e){
            throw new ApiRequestException("Failed to send notification to customer before 15m cancel booking" +e.getMessage());
        }
    }

    @Override
    public void notificationCancelBooking(int reservationID) {
        try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<FirebaseToken> firebaseTokens = firebaseTokenMapper.getFirebaseTokenByCustomerID(id);
        Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
        PushNotificationRequest request = new PushNotificationRequest();
        request.setTitle("Thông báo trạng thái của lần đặt xe");
        request.setTopic("Thông báo trạng thái của lần đặt xe");
        PLO plo = userMapper.getPLOByPLOID(reservation.getPloID());
        request.setMessage("Lần đặt xe của nhà xe: " + plo.getParkingName()+ " đã bị hủy");
        List<Image> images = imageMapper.getImageListByPLOID(reservation.getPloID());
        Image image = images.get(0);
        request.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR3x6Nj8bIgirE0qXYMzXlHHe2AihooslWKegmc9iLVBO7ihisPqJm4pM3k");
        for (FirebaseToken token:
                firebaseTokens) {
            request.setToken(token.getDeviceToken());
            pushNotificationService.sendPushNotificationToToken(request);
        }
        }catch (Exception e){
            throw new ApiRequestException("Failed to send notification to customer cancel booking" +e.getMessage());
        }
    }

    @Override
    public Boolean updateReservationStatusToCancelBooking(int reservationID) {
        try {
            Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
            if(reservation.getStatusID() == 1){
                Timestamp startTime = reservation.getStartTime();
                PLO plo = userMapper.getPLOByPLOID(reservation.getPloID());
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date parsedDate = dateFormat.parse("2023-11-06 23:59:00");
//                Timestamp currentTimestamp = new Timestamp(parsedDate.getTime());

                //cộng time
                long cancelBookingLong = plo.getCancelBookingTime().toLocalTime().toNanoOfDay();
                long startTimeMillis = startTime.getTime();
                long cancelBookingMillis = startTimeMillis + TimeUnit.NANOSECONDS.toMillis(cancelBookingLong);

                Timestamp cancelBooking = new Timestamp(cancelBookingMillis);
                //
                Timestamp before15m = new Timestamp(cancelBooking.getTime() - (15 * 60 * 1000));
                if(currentTimestamp.equals(before15m) && currentTimestamp.before(cancelBooking) || currentTimestamp.after(before15m) && currentTimestamp.before(cancelBooking)){
                    notificationBefore15mCancelBooking(reservationID);
                    return false;
                }
                if(currentTimestamp.equals(cancelBooking) || currentTimestamp.after(cancelBooking)){
                    notificationCancelBooking(reservationID);
                    reservationMapper.updateReservationStatus(5,reservationID,2,currentTimestamp,currentTimestamp);
                    int currentSlot = plo.getCurrentSlot() - 1;
                    parkingMapper.updateCurrentSlot(currentSlot, plo.getPloID());
                    return true;
                }
                return false;
            }
            return true;
        }catch (Exception e){
            throw new ApiRequestException("Failed to update reservation to cancel booking" +e.getMessage());
        }
    }

}
