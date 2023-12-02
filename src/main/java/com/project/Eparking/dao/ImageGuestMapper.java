package com.project.Eparking.dao;

import com.project.Eparking.domain.ImageGuest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ImageGuestMapper {
    void insertImageGuest(int reservationID, String imageLink);
    ImageGuest getImageGuestByReservationID(int reservationID);
}
