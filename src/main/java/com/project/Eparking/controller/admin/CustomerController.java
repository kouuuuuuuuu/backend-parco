package com.project.Eparking.controller.admin;


import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private  final CustomerService customerService;

    @GetMapping("/listCustomer")
    public Response getListCustomer(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            List<CustomerDTO> listCustomers = customerService.getListCustomer(pageNum, pageSize);
            if (listCustomers.isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND.value(), Message.LIST_CUSTOMER_EMPTY, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_CUSTOMER_SUCCESS, listCustomers);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

}
