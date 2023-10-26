package com.project.Eparking.service.interf;

import com.project.Eparking.domain.request.RequestFirebaseToken;

public interface FirebaseTokenService {
    String addDeviceToken(RequestFirebaseToken firebaseToken);
    String deleteTokenByTokenDevice(String deviceToken);
}
