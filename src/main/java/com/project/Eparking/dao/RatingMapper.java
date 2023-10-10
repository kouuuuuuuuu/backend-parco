package com.project.Eparking.dao;

import com.project.Eparking.domain.Rating;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RatingMapper {
    List<Rating> getWithPaginationByPloId(int pageNum, int pageSize, String ploId);
}
