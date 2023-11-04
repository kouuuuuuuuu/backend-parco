package com.project.Eparking.dao;

import com.project.Eparking.domain.FirebaseToken;
import com.project.Eparking.domain.request.RequestFirebaseTokenMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FirebaseTokenMapper {
    void addDeviceToken(RequestFirebaseTokenMapper tokenMapper);
    FirebaseToken getFirebaseTokenByDeviceToken(String deviceToken);
    void deleteTokenByTokenDevice(String deviceToken);

    FirebaseToken getFirebaseTokenByCustomerID(String customerID);
}
