package com.project.Eparking.dao;

import com.project.Eparking.domain.FirebaseToken;
import com.project.Eparking.domain.request.RequestFirebaseTokenMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FirebaseTokenMapper {
    void addDeviceToken(RequestFirebaseTokenMapper tokenMapper);
    FirebaseToken getFirebaseTokenByDeviceToken(String deviceToken);
    void deleteTokenByTokenDevice(String deviceToken);

    List<FirebaseToken> getFirebaseTokenByCustomerID(String customerID);
//    FirebaseToken getFirebaseTokenByCustomerID(String customerID);
    List<FirebaseToken> getTokenByID(String userID);
}
