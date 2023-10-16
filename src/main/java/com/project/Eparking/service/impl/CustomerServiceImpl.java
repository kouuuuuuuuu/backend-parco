package com.project.Eparking.service.impl;

import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.LicensePlateMapper;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.domain.dto.CustomerDTO;
import com.project.Eparking.service.interf.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    private final LicensePlateMapper licensePlateMapper;


    private SimpleDateFormat dateFormat = new SimpleDateFormat("DD/MM/YYYY");
    @Override
    public List<CustomerDTO> getListCustomer(int pageNum, int pageSize) {
        List<CustomerDTO> customerDTOList = new ArrayList<>();

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        //1. Get list customer from database
        List<Customer> listCustomerEntity = customerMapper.getListCustomer(pageNumOffset, pageSize);

        //2. Mapping customer data to customer DTO;
        for (Customer c : listCustomerEntity){
            CustomerDTO customerDTO = new CustomerDTO();
            customerDTO.setCustomerId(c.getCustomerID());
            customerDTO.setPhoneNumber(c.getPhoneNumber());
            customerDTO.setFullName(c.getFullName());
            customerDTO.setStatus(c.getStatus() == 0 ? "Online" : "Offline");
            String registrationDate = dateFormat.format(c.getRegistrationDate());
            customerDTO.setRegistrationDate(registrationDate);

            //2.1. Get licensePlate total number by list customerId;
            List<LicensePlate> licensePlateList = licensePlateMapper.getListLicensePlateByCustomerID(c.getCustomerID());
            customerDTO.setTotalNumber(licensePlateList.size());
            customerDTOList.add(customerDTO);
        }

        return customerDTOList;
    }

}