package com.project.Eparking.controller.admin;

import com.project.Eparking.constant.Message;
import com.project.Eparking.domain.dto.CustAndPloDTO;
import com.project.Eparking.domain.response.Response;
import com.project.Eparking.service.interf.CustAndPloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/custAndPlo")
@RequiredArgsConstructor
public class CustAndPloController {
    private final CustAndPloService custAndPloService;

    @GetMapping("/getTotalCustAndPlo")
    public Response getTotalCustAndPlo() {
        CustAndPloDTO totalCustAndPlo = custAndPloService.getTotalCustAndPlo();
        return new Response(HttpStatus.OK.value(), Message.GET_TOTAL_CUST_AND_PLO_SUCCESS, totalCustAndPlo);
    }
}
