package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.RatingDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            List<RatingDTO> ratingDTOS = ratingService.getRatingWithPaginationByPloId(pageNum, pageSize, ploId);
            return new Response(HttpStatus.OK.value(), Message.GET_PAGINATION_RATING_BY_PLO_ID_SUCCESS, ratingDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
