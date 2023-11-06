package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.Payment;
import com.project.Eparking.domain.dto.CustomerWalletDTO;
import com.project.Eparking.domain.dto.PloDetailForCustomerDTO;
import com.project.Eparking.domain.dto.ReservationDTO;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerTransaction;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.request.RequestFindParkingList;
import com.project.Eparking.domain.response.*;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.PaymentService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerControllerRole {
    private final CustomerService customerService;
    private final PaymentService paymentService;
    private final ReservationService reservationService;

//    @PostMapping("/updateProfile")
//    public ResponseEntity<String> updateProfile(@RequestBody RequestCustomerUpdateProfile profile){
//        try {
//            return ResponseEntity.ok(customerService.updateCustomerProfile(profile));
//        }catch (ApiRequestException e){
//            throw e;
//        }
//    }
//    @PutMapping("/updatePassword")
//    public ResponseEntity<List<String>> updateNewPassword(@RequestBody RequestChangePasswordUser user){
//        try {
//            return ResponseEntity.ok(customerService.updatePassswordCustomer(user));
//        }catch (ApiRequestException e){
//            throw e;
//        }
//    }
    @GetMapping("/returnPayment")
    public ResponseEntity<?> returnPaymentCustomer(HttpServletRequest request){
        try {
            return paymentService.paymentReturnCustomer(request);
        }catch (ApiRequestException e) {
            throw e;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/createPayment")
    public ResponseEntity<?> createPaymentCustomer(HttpServletRequest request, @RequestBody RequestCustomerTransaction transaction){
        try {
            return customerService.createPaymentCustomer(request,transaction);
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/walletScreen")
    public ResponseEntity<ResponseWalletScreen> responseWalletScreen(){
        try {
            return ResponseEntity.ok(customerService.responseWalletScreen());
        }catch (ApiRequestException e){
            throw e;
        }
    }

    @GetMapping("/customerBalance")
    public Response getCustomerBalance(){
        try {
            CustomerWalletDTO customerWalletDTO = customerService.getCustomerBalance();
            if (Objects.isNull(customerWalletDTO)){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.GET_CUSTOMER_BALANCE_FAIL, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_CUSTOMER_BALANCE_SUCCESS, customerWalletDTO);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_GET_CUSTOMER_BALANCE, null);
        }
    }

    @GetMapping("/detailPloForCustomer")
    public Response getPloDetailForCustomer(@RequestParam("ploID") String ploID){
        try{
            PloDetailForCustomerDTO ploDetailForCustomerDTO = customerService.getPloDetailForCustomer(ploID);
            if (Objects.isNull(ploDetailForCustomerDTO)){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.GET_PLO_DETAIL_FOR_CUSTOMER_FAIL, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_PLO_DETAIL_FOR_CUSTOMER_SUCCESS, ploDetailForCustomerDTO);
        } catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_GET_PLO_DETAIL_FOR_CUSTOMER, null);
        }
    }
    @GetMapping("/findParkingList")
    public ResponseEntity<?> findParkingList(@RequestParam Double latitude,@RequestParam Double longitude,@RequestParam Double radius,@RequestParam int method){
        try {
            if(method == 1){
                RequestFindParkingList requestFindParkingList = new RequestFindParkingList(latitude,longitude,radius);
                return ResponseEntity.ok(reservationService.nearestParkingList(requestFindParkingList));
            }else if(method == 2){
                RequestFindParkingList requestFindParkingList = new RequestFindParkingList(latitude,longitude,radius);
                return ResponseEntity.ok(reservationService.cheapestParkingList(requestFindParkingList));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid method value.");
            }
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getScreen")
    public ResponseEntity<ResponseScreenReservation> getScreenReservation(){
        try {
            return ResponseEntity.ok(reservationService.getScreenCustomer());
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getListMethodByTime")
    public ResponseEntity<List<ResponseMethodByTime>> getMethodByTime(@RequestParam String ploID){
        try {
            return  ResponseEntity.ok(reservationService.getListMethodByTime(ploID));
        }catch (ApiRequestException e){
            throw e;
        }
    }
//    @PostMapping("/notiCancelBookingBefore15m")
//    public ResponseEntity<String> notiCancelBookingBefore15m(@RequestParam int reservationID){
//        try {
//            customerService.notificationBefore15mCancelBooking(reservationID);
//            return ResponseEntity.ok("send notification successfully!");
//        }catch (ApiRequestException e){
//            throw e;
//        }
//    }
//    @PostMapping("/notiCancelBooking")
//    public ResponseEntity<String> notiCancelBooking(@RequestParam int reservationID){
//        try {
//            customerService.notificationCancelBooking(reservationID);
//            return ResponseEntity.ok("send notification successfully!");
//        }catch (ApiRequestException e){
//            throw e;
//        }
//    }
    @PutMapping("/updateReservationToCancel")
    ResponseEntity<Boolean> updateReservationCancelBooking(@RequestParam int reservationID){
        try {
            return ResponseEntity.ok(customerService.updateReservationStatusToCancelBooking(reservationID));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
