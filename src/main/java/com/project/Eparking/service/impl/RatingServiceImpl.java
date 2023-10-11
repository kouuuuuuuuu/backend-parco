package com.project.Eparking.service.impl;

import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.RatingMapper;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.dto.RatingDTO;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;

    private final CustomerMapper customerMapper;
    @Override
    public List<RatingDTO> getRatingWithPaginationByPloId(int pageNum, int pageSize, String ploId) throws Exception {
        // 1. Get list rating entity pagination
        int pageNumOffset = (pageNum -1) == 0 ? 0 : (pageNum-1) * pageSize;
        List<Rating> ratingList = ratingMapper.getWithPaginationByPloId(pageNumOffset,pageSize,ploId);

        if (ratingList.isEmpty()){
            throw new Exception("Not found rating of this parking lot owner");
        }

        // 2. Mapping to dto
        List<RatingDTO> ratingDTOS = new ArrayList<>();
        for (Rating rating: ratingList){
            RatingDTO ratingDTO = new RatingDTO();
            ratingDTO.setRatingID(rating.getRatingID());
            Customer customer = customerMapper.getCustomerById(rating.getCustomerID());
            ratingDTO.setFullName(customer.getFullName());
            ratingDTO.setContent(rating.getContent());
            ratingDTO.setStar(rating.getStar());
            ratingDTO.setFeedbackDate(rating.getFeedbackDate());
            ratingDTOS.add(ratingDTO);
        }
        return ratingDTOS;
    }
}
