package com.project.Eparking.security;

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

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
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
                        "/swagger-ui.html", "/webjars/**", "/v2/api-docs", "/swagger-resources/**").permitAll();
        http.authorizeRequests().antMatchers(GET, "/PLO/profile").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/PLO/updateProfile").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/PLO/changePassword").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkoutReservation").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(PUT, "/reservation/checkinReservation").hasAnyAuthority("PLO","CUSTOMER");
        http.authorizeRequests().antMatchers(POST, "/parking/registerParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingInformation").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getParkingStatusID").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getReservationDetail").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingStatusID/**").hasAnyAuthority("PLO", "ADMIN");
        http.authorizeRequests().antMatchers(GET, "/parking/showListVehicleInParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/checkPaymentPLO").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/closeParkingStatus").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/openParkingStatus").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/plo/getPloByParkingStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/searchListPloByKeywords").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getRegistrationDetail").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/updatePloStatus").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/getParkingInformation").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/parking/getParkingSetting").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/parking/updateParkingSetting").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(POST, "/parking/getAllReservationMethod").hasAnyAuthority("PLO","ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/getRatingList").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/plo/getDetailPlo").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/rating/getByPloId").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/PLO/checkPLOTransfer").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(PUT, "/PLO/checkOTPcodeTransferParking").hasAnyAuthority("PLO");
        http.authorizeRequests().antMatchers(GET, "/customer/listCustomer").hasAnyAuthority("ADMIN");
        http.authorizeRequests().antMatchers(GET, "/plo/getRegistrationByParkingStatus").hasAnyAuthority("ADMIN");
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
