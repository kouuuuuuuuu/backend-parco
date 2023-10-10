package com.project.Eparking.service.interf;

import com.project.Eparking.domain.dto.RatingDTO;

import java.util.List;

public interface RatingService {
    List<RatingDTO> getRatingWithPaginationByPloId(int pageNum, int pageSize, String ploId) throws Exception;
}
