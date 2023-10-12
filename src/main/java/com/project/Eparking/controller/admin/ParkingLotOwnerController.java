package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
import com.project.Eparking.domain.dto.PloRegistrationDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/plo")
@RequiredArgsConstructor
public class ParkingLotOwnerController {

    private final ParkingLotOwnerService parkingLotOwnerService;

    @GetMapping("/getPloByParkingStatus")
    public Response getPloByParkingStatus(@RequestParam("status") int status,
                                           @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        try {
            List<ListPloDTO> listPlo = parkingLotOwnerService.getPloByParkingStatus(status, pageNum, pageSize);
            if (listPlo.isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_STATUS, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_PLO_SUCCESS, listPlo);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/searchListPloByKeywords")
    public Response getListParkingLotOwnerByKeywords(@RequestParam("keyword") String keyword,
                                                     @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                     @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        try {
            List<ListPloDTO> listPloDTOS = parkingLotOwnerService.getListPloByKeywords(keyword, pageNum, pageSize);
            if (listPloDTOS.isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_KEY_WORD, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_PLO_SUCCESS, listPloDTOS);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/getDetailPlo")
    public Response getDetailPloById(@RequestParam("ploID") String ploId){
        try {
            ParkingLotOwnerDTO detailPloDto = parkingLotOwnerService.getDetailPloById(ploId);
            if (Objects.isNull(detailPloDto)){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_ID, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_PLO_DETAIL_SUCCESS, detailPloDto);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/getRegistrationDetail")
    public Response getRegistrationDetail(@RequestParam("ploID") String polId){
        try {
            PloRegistrationDTO ploRegistrationDTO = parkingLotOwnerService.getPloRegistrationByPloId(polId);
            if (Objects.isNull(ploRegistrationDTO)){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_ID, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_REGISTRATION_PLO_SUCCESS, ploRegistrationDTO);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
