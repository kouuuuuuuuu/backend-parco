package com.project.Eparking.controller;

import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.response.ResponsePLOProfile;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    @GetMapping("/getRatingList")
    public ResponseEntity<List<Rating>> getListRating(){
        try{
            return ResponseEntity.ok(ratingService.getRatingListByPLOID());
        }catch (Exception e){
            throw e;
        }
    }
}
