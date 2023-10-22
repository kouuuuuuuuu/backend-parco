package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.*;
import com.project.Eparking.domain.request.RequestMonthANDYear;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.domain.response.ResponseTop5Parking;
import com.project.Eparking.domain.response.ResponseTop5Revenue;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import com.project.Eparking.service.interf.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/plo")
@RequiredArgsConstructor
public class ParkingLotOwnerController {

    private final ParkingLotOwnerService parkingLotOwnerService;
    private final ReservationService reservationService;


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
                                                       @RequestParam(name = "pageSize", defaultValue = "5") int pageSize,
                                                       @RequestParam(name = "keywords", defaultValue = "") String keywords){
        try {
            Page<ListPloDTO> listRegistration = parkingLotOwnerService.getListRegistrationByParkingStatus(status, pageNum, pageSize, keywords);
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

    @GetMapping("/getRegistrationHistory")
    public Response getRegistrationHistory(@RequestParam(name = "keywords", defaultValue = "") String keywords,
                                           @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                           @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        int status = 3;
        try {
            Page<RegistrationHistoryDTO> listRegistration = parkingLotOwnerService.getListRegistrationHistory(status, pageNum, pageSize, keywords);
            if (listRegistration.getContent().isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_REGISTRATION_HISTORY, null);
            }
            return new Response(HttpStatus.OK.value(), Message.GET_LIST_REGISTRATION_HISTORY_SUCCESS, listRegistration);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
    @GetMapping("/getTop5Parking")
    public ResponseEntity<List<ResponseTop5Parking>> getTop5ParkingMostReservation(@RequestParam String month, @RequestParam String year){
        try {
            RequestMonthANDYear requestMonthANDYear = new RequestMonthANDYear(year + "-" + month);
            return ResponseEntity.ok(reservationService.getTop5Parking(requestMonthANDYear));
        }catch (ApiRequestException e){
            throw e;
        }
    }
    @GetMapping("/getTop5ParkingRevenue")
    public ResponseEntity<List<ResponseTop5Revenue>> getTop5ParkingMostRevenue(@RequestParam String month, @RequestParam String year){
        try {
            RequestMonthANDYear requestMonthANDYear = new RequestMonthANDYear(year + "-" + month);
            return ResponseEntity.ok(reservationService.getTop5Revenue(requestMonthANDYear));
        }catch (ApiRequestException e){
            throw e;
        }
    }
}
