package com.project.Eparking.service.impl;

import com.project.Eparking.dao.FirebaseTokenMapper;
import com.project.Eparking.domain.FirebaseToken;
import com.project.Eparking.domain.request.RequestFirebaseToken;
import com.project.Eparking.domain.request.RequestFirebaseTokenMapper;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.FirebaseTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FirebaseTokenImpl implements FirebaseTokenService {

    private final FirebaseTokenMapper tokenMapper;
    @Override
    public String addDeviceToken(RequestFirebaseToken firebaseToken) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            FirebaseToken check = tokenMapper.getFirebaseTokenByDeviceToken(firebaseToken.getDeviceToken());
            if(check!=null){
                return "This token device is exist";
            }
            RequestFirebaseTokenMapper firebaseTokenMapper = new RequestFirebaseTokenMapper();
            firebaseTokenMapper.setDeviceToken(firebaseToken.getDeviceToken());
            firebaseTokenMapper.setRecipientID(id);
            if(id.startsWith("CU") || id.startsWith("cu")){
                firebaseTokenMapper.setRecipientType("CUSTOMER");
            }else if(id.startsWith("PL") ||id.startsWith("pl")){
                firebaseTokenMapper.setRecipientType("PLO");
            }else if(id.startsWith("AD") || id.startsWith("ad")){
                firebaseTokenMapper.setRecipientType("ADMIN");
            }
            tokenMapper.addDeviceToken(firebaseTokenMapper);
            return "Add token successfully";
        }catch (Exception e){
            throw new ApiRequestException("Failed add device token." + e.getMessage());
        }
    }

    @Override
    public String deleteTokenByTokenDevice(String deviceToken) {
        try{
            FirebaseToken check = tokenMapper.getFirebaseTokenByDeviceToken(deviceToken);
            if(check==null){
                throw new ApiRequestException("This token device is not exist");
            }
            tokenMapper.deleteTokenByTokenDevice(deviceToken);
            return "Delete token device successfully";
        }catch (Exception e){
            throw new ApiRequestException("Failed to delete device token." + e.getMessage());
        }
    }
}
