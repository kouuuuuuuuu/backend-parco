package com.project.Eparking.security;

import io.netty.handler.codec.http.cors.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configurationSource = new CorsConfiguration();
        configurationSource.setAllowedOrigins(List.of("*"));
        configurationSource.setAllowedMethods(List.of("*"));
        configurationSource.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configurationSource);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        http.cors().and();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        http.authorizeRequests()
                .antMatchers("/user/loginUser/**",
                        "/user/regiterUser/**",
                        "/user/confirmRegisterOTP",
                        "/user/checkPhoneNumber",
                        "/user/checkOTPcode",
                        "/user/updatePassword",
                        "/parking/getReturnPayment",
                        "/customer/returnPayment",
                        "/user/sendNoti",
                        "/privateReservation",
                        "/swagger-ui.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll();

        http.authorizeRequests().antMatchers(PUT, "/PLO/getBalance").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/PLO/getRevenue").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/PLO/requestWithdrawal").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/PLO/getSumByDate").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkoutReservation").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkinReservation").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkoutReservationWithLicensePlate").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkinReservationWithLicensePlate").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(POST, "/parking/registerParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingInformation").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getParkingStatusID").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getReservationDetail").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingStatusID/**").hasAnyAuthority("PLO", "ADMIN");
        http.authorizeRequests().antMatchers(GET, "/parking/showListVehicleInParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/checkPaymentPLO").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/paymentRegisterParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/closeParkingStatus").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/openParkingStatus").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/plo/getPloByParkingStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/searchListPloByKeywords").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getRegistrationDetail").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/plo/updatePloStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/getParkingInformation").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getParkingSetting").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking//showListVehicleInParkingByParkingStatus").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(POST, "/parking/updateParkingSetting").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingSetting").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(POST, "/parking/getAllReservationMethod").hasAnyAuthority("PLO","ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/getRatingList").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/plo/getDetailPlo").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getChartPLO").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/rating/getByPloId").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/checkPLOTransfer").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/PLO/checkOTPcodeTransferParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/customer/listCustomer").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/customer/registerChartCustomer").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/customer/listCustomerByName").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getRegistrationByParkingStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getRegistrationHistory").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getTop5Parking").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getTop5ParkingRevenue").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/ploTransaction/getListWithdrawalByStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(PUT, "/ploTransaction/updateWithdrawalStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/custAndPlo/getTotalCustAndPlo").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/ploTransaction/searchWithdrawalByKeyword").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/user/getNotifcations").hasAnyAuthority("ADMIN","PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/user/addDeviceToken").hasAnyAuthority("ADMIN","PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/user/deleteDeviceToken").hasAnyAuthority("ADMIN","PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/reservationHistory").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/reservationHistoryDetail").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/licensePlate/getLicensePlate").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(DELETE, "/licensePlate/deleteLicensePlate").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/licensePlate/addLicensePlate").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/customer/createPayment").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/walletScreen").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/rating/getRatingOfCustomer").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/rating/sendRating").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/customerBalance").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/getInforReservationByLicensePlate").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/reservation/cancelReservation").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/reservation/bookingReservation").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/detailPloForCustomer").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/findParkingList").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/getScreen").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/user/getProfileUser").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/user/updateProfileUser").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/user/changePassword").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/customer/getListMethodByTime").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().antMatchers(GET, "/reservation/bookingDetail").hasAnyAuthority("CUSTOMER");
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


}
