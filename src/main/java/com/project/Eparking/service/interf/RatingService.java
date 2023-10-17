package com.project.Eparking.service.interf;

import com.project.Eparking.domain.Rating;

import com.project.Eparking.domain.dto.RatingDTO;
import com.project.Eparking.domain.response.Page;


import java.util.List;

public interface RatingService {

    List<Rating> getRatingListByPLOID();

    Page<RatingDTO> getRatingWithPaginationByPloId(int pageNum, int pageSize, String ploId) throws Exception;
}
