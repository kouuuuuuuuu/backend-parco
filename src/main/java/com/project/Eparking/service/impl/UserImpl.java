package com.project.Eparking.service.impl;

import com.project.Eparking.dao.UserMapper;
import com.project.Eparking.domain.Admin;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.response.ResponseAdmin;
import com.project.Eparking.domain.response.ResponseCustomer;
import com.project.Eparking.domain.response.ResponsePLO;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserImpl implements UserService, UserDetailsService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(username.startsWith("cu") || username.startsWith("CU")){
            Customer customer = userMapper.getCustomerByCustomerID(username);
            if(customer == null){
                log.error("Customer not found in the database");
                throw new UsernameNotFoundException("Customer not found in the database");
            }else{
                log.info("Customer found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(customer.getRole()));
            return new org.springframework.security.core.userdetails.User(customer.getCustomerID(), customer.getPassword(), authorities);
        }else if (username.startsWith("pl") ||username.startsWith("PL")){
            PLO plo = userMapper.getPLOByPLOID(username);
            if(plo == null){
                log.error("plo not found in the database");
                throw new UsernameNotFoundException("plo not found in the database");
            }else{
                log.info("plo found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(plo.getRole()));
            return new org.springframework.security.core.userdetails.User(plo.getPloID(), plo.getPassword(), authorities);
        }else if (username.startsWith("ad") ||username.startsWith("AD")){
            Admin Admin = userMapper.getAdminByAdminID(username);
            if(Admin == null){
                log.error("Admin not found in the database");
                throw new UsernameNotFoundException("Admin not found in the database");
            }else{
                log.info("Admin found in the database: {}", username);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(Admin.getRole()));
            return new org.springframework.security.core.userdetails.User(Admin.getAdminID(), Admin.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
    @Override
    public Admin getAdminByAdminID(String adminID) {
        try {
            return userMapper.getAdminByAdminID(adminID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Admin by AdminID" +e.getMessage());
        }
    }
    @Override
    public Customer getCustomerByCustomerID(String customerID) {
        try {
            return userMapper.getCustomerByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Customer by customerID" +e.getMessage());
        }
    }

    @Override
    public PLO getPLOByPLOID(String ploID) {
        try {
            return userMapper.getPLOByPLOID(ploID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get PLO by PLOID" +e.getMessage());
        }
    }

    @Override
    public ResponseCustomer getResponseCustomerByCustomerID(String customerID) {
        try {
            return userMapper.getResponseCustomerByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get Customer Response by customerID" +e.getMessage());
        }
    }

    @Override
    public ResponsePLO getPLOResponseByPLOID(String ploID) {
        try {
            return userMapper.getPLOResponseByPLOID(ploID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get PLO Response by PLOID" +e.getMessage());
        }
    }

    @Override
    public ResponseAdmin getAdminResponseByAdminID(String adminID) {
        try{
            return userMapper.getAdminResponseByAdminID(adminID);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get Admin Response by AdminID" +e.getMessage());
        }
    }
}
