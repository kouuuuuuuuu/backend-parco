package com.project.Eparking.dao;

import com.project.Eparking.domain.PriceMethod;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PriceMethodMapper {

    void create(PriceMethod priceMethod);
}
