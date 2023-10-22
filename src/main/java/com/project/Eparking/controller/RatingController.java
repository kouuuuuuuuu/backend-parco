package com.project.Eparking.controller;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.dto.CreateRatingDTO;
import com.project.Eparking.domain.dto.CustomerRatingDTO;
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

    @GetMapping("/getRatingOfCustomer")
    public Response getRatingOfCustomer(@RequestParam("ploID") String ploID){
        try {
            List<CustomerRatingDTO> customerRatingDTOS = ratingService.getRatingOfCustomer(ploID);
            if (customerRatingDTOS.isEmpty()) {
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_RATING_OF_CUSTOMER, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_RATING_OF_CUSTOMER_SUCCESS, customerRatingDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @PostMapping("/sendRating")
    public Response sendRating(@RequestBody CreateRatingDTO createRatingDTO){
        try {
            String message = ratingService.createRating(createRatingDTO);
            return new Response(HttpStatus.OK.value(), message, null);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), Message.ERROR_CREATE_RATING, null);
        }
    }

}
