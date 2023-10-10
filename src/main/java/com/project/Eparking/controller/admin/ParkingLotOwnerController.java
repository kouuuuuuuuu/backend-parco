package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plo")
@RequiredArgsConstructor
public class ParkingLotOwnerController {

    private final ParkingLotOwnerService parkingLotOwnerService;

    @GetMapping("/getDetailPlo")
    public Response getDetailPloById(@RequestParam("ploID") String ploId){
        try {
            ParkingLotOwnerDTO detailPloDto = parkingLotOwnerService.getDetailPloById(ploId);
            return new Response(HttpStatus.OK.value(), Message.GET_PLO_DETAIL_SUCCESS, detailPloDto);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
