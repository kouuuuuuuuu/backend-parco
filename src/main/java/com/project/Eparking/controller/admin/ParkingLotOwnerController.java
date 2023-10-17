package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.ParkingLotOwnerDTO;
import com.project.Eparking.domain.dto.PloRegistrationDTO;
import com.project.Eparking.domain.dto.UpdatePloStatusDTO;
import com.project.Eparking.domain.response.Page;
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
            Page<ListPloDTO> listPlo = parkingLotOwnerService.getPloByParkingStatus(status, pageNum, pageSize);
            if (listPlo.getContent().isEmpty()){
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
                                                     @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
                                                     @RequestParam("parkingStatus") int parkingStatus) {
        try {
            Page<ListPloDTO> ploDTOSpage = parkingLotOwnerService.getListPloByKeywords(keyword, parkingStatus, pageNum, pageSize);
            if (ploDTOSpage.getContent().isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_KEY_WORD, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_PLO_SUCCESS, ploDTOSpage);
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
    public Response getRegistrationDetail(@RequestParam("ploID") String polId) {
        try {
            PloRegistrationDTO ploRegistrationDTO = parkingLotOwnerService.getPloRegistrationByPloId(polId);
            if (Objects.isNull(ploRegistrationDTO)) {
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_BY_ID, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_REGISTRATION_PLO_SUCCESS, ploRegistrationDTO);
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/getRegistrationByParkingStatus")
    public Response getListRegistrationByParkingStatus(@RequestParam("status")int status,
                                                       @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                                       @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            Page<ListPloDTO> listRegistration = parkingLotOwnerService.getListRegistrationByParkingStatus(status, pageNum, pageSize);
            if (listRegistration.getContent().isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_REGISTRATION_BY_PARKING_STATUS, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_REGISTRATION_SUCCESS, listRegistration);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @PutMapping("/updatePloStatus")
    public Response updatePloStatus(@RequestBody UpdatePloStatusDTO updatePloStatusDTO){
        try {
            boolean isUpdateSuccess = parkingLotOwnerService.updatePloStatusById(updatePloStatusDTO);
            if(!isUpdateSuccess){
                return  new Response(HttpStatus.NOT_FOUND.value(), Message.UPDATE_STATUS_PLO_BY_ID_FAIL, null);
            }
            return new Response(HttpStatus.OK.value(), Message.UPDATE_STATUS_PLO_BY_ID_SUCCESS, null);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
