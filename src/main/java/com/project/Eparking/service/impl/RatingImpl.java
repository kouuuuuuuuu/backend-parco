package com.project.Eparking.service.impl;

import com.project.Eparking.dao.RatingMapper;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class RatingImpl implements RatingService {
    private final RatingMapper ratingMapper;
    @Override
    public List<Rating> getRatingListByPLOID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            return ratingMapper.getRatingListByPLOID(id);
        }catch (Exception e){
            throw new ApiRequestException("Failed to get rating list by ploID" + e.getMessage());
        }
    }
}
