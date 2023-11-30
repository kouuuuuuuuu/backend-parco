package com.project.Eparking.dao;

import com.project.Eparking.domain.PriceMethod;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceMethodMapper {
    PriceMethod getPriceMethodByReservationID(int reservationID);
    void updateTotalPrice(double total, int reservationID);

    void create(PriceMethod priceMethod);
}
