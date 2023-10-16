package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.dto.RatingDTO;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.domain.response.ResponsePLOProfile;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rating")
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    @GetMapping("/getByPloId")
    public Response getRatingByPloId(@RequestParam("ploId") String ploId,
                                     @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                     @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            Page<RatingDTO> ratingDTOS = ratingService.getRatingWithPaginationByPloId(pageNum, pageSize, ploId);
            return new Response(HttpStatus.OK.value(), Message.GET_PAGINATION_RATING_BY_PLO_ID_SUCCESS, ratingDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
