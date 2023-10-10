package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plo")
@RequiredArgsConstructor
public class ParkingLotOwnerController {

    private final ParkingLotOwnerService parkingLotOwnerService;

    @GetMapping("/getListPloByStatus")
    public Response getListParkingLotOwner(@RequestParam("status") int status,
                                           @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        try {
            List<ListPloDTO> listPlo = parkingLotOwnerService.getListPloByStatus(status, pageNum, pageSize);
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_PLO_SUCCESS, listPlo);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/searchListPloByKeywords")
    public Response getListParkingLotOwnerByKeywords(@RequestParam("keyword") String keyword,
                                                     @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                     @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            List<ListPloDTO> listPloDTOS = parkingLotOwnerService.getListPloByKeywords(keyword, pageNum, pageSize);
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_PLO_SUCCESS, listPloDTOS);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
