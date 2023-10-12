package com.project.Eparking.dao;

import com.project.Eparking.domain.Image;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImageMapper {
    List<Image> getImageListByPLOID(String ploID);
    List<Image> getImageByPloId(String ploId);
    void deleteImageByPLOID(String ploID);
    void batchInsertImages(List<Image> image);
}
