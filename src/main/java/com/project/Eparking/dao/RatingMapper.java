package com.project.Eparking.dao;

import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.response.ResponseRating;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RatingMapper {
    List<ResponseRating> getRatingListByPLOID(String ploID);
    List<Rating> getWithPaginationByPloId(int pageNum, int pageSize, String ploId);
    Integer countRecords(String ploId);
    void sendRating(Rating rating);
}
