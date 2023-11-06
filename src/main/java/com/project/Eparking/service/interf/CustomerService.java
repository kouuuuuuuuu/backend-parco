package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.dto.PloDetailForCustomerDTO;
import com.project.Eparking.domain.request.*;
import com.project.Eparking.domain.dto.CustomerWalletDTO;
import com.project.Eparking.domain.request.RequestChangePasswordUser;
import com.project.Eparking.domain.request.RequestCustomerTransaction;
import com.project.Eparking.domain.request.RequestCustomerUpdateProfile;
import com.project.Eparking.domain.response.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CustomerService {
    Page<CustomerDTO> getListCustomer(int pageNum, int pageSize);
    Page<CustomerDTO> getListCustomerByName(String name, int pageNum, int pageSize);
    ResponseCustomer getResponseCustomerByCustomerID();
    String updateCustomerProfile(RequestCustomerUpdateProfile profile);
    List<String> updatePassswordCustomer(RequestChangePasswordUser customer);
    ResponseEntity<?> createPaymentCustomer(HttpServletRequest req,RequestCustomerTransaction transaction);
    ResponseWalletScreen responseWalletScreen();
    List<WeekData> countRecordsByWeekCustomer(RequestMonthANDYear requestMonthANDYear);
//    CustomerWalletDTO getCustomerBalance();
    CustomerWalletDTO getCustomerBalance();
    PloDetailForCustomerDTO getPloDetailForCustomer(String ploID);
    void notificationBefore15mCancelBooking(int reservationID);
    void notificationCancelBooking(int reservationID);
    Boolean updateReservationStatusToCancelBooking(int reservationID);

}
