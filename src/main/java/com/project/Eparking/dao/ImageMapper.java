package com.project.Eparking.dao;

import com.project.Eparking.domain.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImageMapper {
    List<Image> getImageByPloId(String ploId);
}
