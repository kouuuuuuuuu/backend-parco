package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.PloWithdrawalDTO;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.PloTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/ploTransaction")
@RequiredArgsConstructor
public class PloTransactionController {

    private final PloTransactionService ploTransactionService;

    @PutMapping("/updateWithdrawalStatus")
    public Response updateWithdrawalStatus(@RequestParam("status") int status,
                                           @RequestParam("transactionID") int transactionId){
        try {
            boolean isUpdateSuccess = ploTransactionService.updateWithdrawalStatus(transactionId,status);
            if(!isUpdateSuccess){
                return  new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_WITHDRAWAL_BY_STATUS, null);
            }
            return new Response(HttpStatus.OK.value(), Message.UPDATE_WITHDRAWAL_STATUS_SUCCESS, null);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    @GetMapping("/searchWithdrawalByKeyword")
    public Response searchWithdrawalByKeyword(@RequestParam("status") int status,
                                              @RequestParam(name = "keyword", defaultValue = "") String keywords,
                                              @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(name = "pageSize", defaultValue = "5") int pageSize){
        try {
            Page<PloWithdrawalDTO> ploWithdrawalDTOPage = ploTransactionService.searchWithdrawalByKeyword(status, keywords, pageNum, pageSize);
            if (ploWithdrawalDTOPage.getContent().isEmpty()){
                return new Response(HttpStatus.NOT_FOUND.value(), Message.NOT_FOUND_PLO_TRANSACTION_BY_KEYWORD, null);
            }
            return new Response(HttpStatus.OK.value(), Message.SEARCH_PLO_TRANSACTION_BY_KEYWORD_SUCCESS, ploWithdrawalDTOPage);
        }catch (Exception e){
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
