package com.project.Eparking.controller.admin;


import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.dto.Top5CustomerDTO;
import com.project.Eparking.domain.request.RequestMonthANDYear;

import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.domain.response.Response4week;
import com.project.Eparking.domain.response.WeekData;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.CustomerService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private  final CustomerService customerService;
    private final ReservationService reservationService;

    @GetMapping("/listCustomer")
    public Response getListCustomer(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            Page<CustomerDTO> listCustomers = customerService.getListCustomer(pageNum, pageSize);
            if (listCustomers.getContent().isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND.value(), Message.LIST_CUSTOMER_EMPTY, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_CUSTOMER_SUCCESS, listCustomers);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/listCustomerByName")
    public Response getListCustomerByName(@RequestParam("name") String name,
                                          @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                          @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            Page<CustomerDTO> listCustomers = customerService.getListCustomerByName(name, pageNum, pageSize);
            if (listCustomers.getContent().isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_CUSTOMER_BY_NAME, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_CUSTOMER_SUCCESS, listCustomers);
        }catch (Exception e){
          return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/getTop5Reservation")
    public Response getTop5CustomerReservation(@RequestParam String month,
                                               @RequestParam String year) throws ParseException {
        try {
            RequestMonthANDYear requestMonthANDYear = new RequestMonthANDYear(year + "-" + month);
            List<Top5CustomerDTO> top5CustomerDTOS = reservationService.getTop5Customer(requestMonthANDYear);
            return new Response (HttpStatus.OK.value(), Message.GET_TOP_5_CUSTOMER_SUCCESS, top5CustomerDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.GET_TOP_5_CUSTOMER_FAIL, null);
        }
    }
    @GetMapping("/registerChartCustomer")
    public ResponseEntity<List<WeekData>> getCustomerRegisterChart(@RequestParam String month, @RequestParam String year){
        try{
            RequestMonthANDYear requestMonthANDYear = new RequestMonthANDYear(year + "-" + month);
            return ResponseEntity.ok(customerService.countRecordsByWeekCustomer(requestMonthANDYear));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
