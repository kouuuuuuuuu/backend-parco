package com.project.Eparking.service.impl;

import com.project.Eparking.constant.Message;
import com.project.Eparking.dao.CustomerMapper;
import com.project.Eparking.dao.RatingMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.domain.Customer;
import com.project.Eparking.domain.Rating;
import com.project.Eparking.domain.Reservation;
import com.project.Eparking.domain.dto.CreateRatingDTO;
import com.project.Eparking.domain.dto.CustomerRatingDTO;
import com.project.Eparking.domain.dto.ListPloDTO;
import com.project.Eparking.domain.dto.RatingDTO;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.domain.response.RatingResponse;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingImpl implements RatingService {
    private final RatingMapper ratingMapper;

    private final CustomerMapper customerMapper;

    private final ReservationMapper reservationMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
    @Override
    public List<RatingResponse> getRatingListByPLOID() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String id = authentication.getName();
            List<Rating> rating = ratingMapper.getRatingListByPLOID(id);
            List<RatingResponse> ratingResponses =new ArrayList<>();
            for (Rating ratingList:
                    rating) {
                String date = Objects.nonNull(ratingList.getFeedbackDate())? dateFormat.format(ratingList.getFeedbackDate()) : "";
                ratingResponses.add(new RatingResponse(ratingList.getRatingID(),ratingList.getStar(),ratingList.getContent(),ratingList.getCustomerID(),ratingList.getFullName(),ratingList.getPloID(),ratingList.getReservationID(),date));
            }
            return ratingResponses;
        }catch (Exception e){
            throw new ApiRequestException("Failed to get rating list by ploID" + e.getMessage());
        }
    }
    @Override
    public Page<RatingDTO> getRatingWithPaginationByPloId(int pageNum, int pageSize, String ploId) throws Exception {
        // 1. Get list rating entity pagination
        int pageNumOffset = (pageNum -1) == 0 ? 0 : (pageNum-1) * pageSize;
        List<Rating> ratingList = ratingMapper.getWithPaginationByPloId(pageNumOffset,pageSize,ploId);
        // 2.1 Mapping to dto
        List<RatingDTO> ratingDTOS = new ArrayList<>();
        for (Rating rating: ratingList){
            RatingDTO ratingDTO = new RatingDTO();
            ratingDTO.setRatingID(rating.getRatingID());
            Customer customer = customerMapper.getCustomerById(rating.getCustomerID());
            ratingDTO.setFullName(customer.getFullName());
            ratingDTO.setContent(rating.getContent());
            ratingDTO.setStar(rating.getStar());
            ratingDTO.setFeedbackDate(Objects.nonNull(rating.getFeedbackDate())?
                    dateFormat.format(rating.getFeedbackDate()) : "");
            ratingDTOS.add(ratingDTO);
        }
        int totalRecords = this.countRecords(ploId);
        return new Page<RatingDTO>(ratingDTOS, pageNum, pageSize, totalRecords);
    }

    @Override
    public List<CustomerRatingDTO> getRatingOfCustomer(String ploID) {
        List<CustomerRatingDTO> customerRatingDTOList = new ArrayList<>();

        //1. Get rating list by ploID
        List<Rating> ratingList = ratingMapper.getRatingListByPLOID(ploID);
        if (ratingList.isEmpty()){
            return customerRatingDTOList;
        }
        //1.1 Sort ratingList by feedbackDate in descending order (from newest to oldest)
        ratingList.sort((r1, r2) -> r2.getFeedbackDate().compareTo(r1.getFeedbackDate()));
        //2. Mapping data
        for (Rating rating : ratingList){
            //2.1 Get customer information from rating customer ID
            Customer customer = customerMapper.getCustomerById(rating.getCustomerID());
            CustomerRatingDTO customerRatingDTO = new CustomerRatingDTO();
            customerRatingDTO.setCustomerID(customer.getCustomerID());
            customerRatingDTO.setCustomerName(customer.getFullName());
            customerRatingDTO.setRating(rating.getStar());
            customerRatingDTO.setContent(rating.getContent());
            customerRatingDTO.setFeedbackDate(Objects.nonNull(rating.getFeedbackDate())?
                    dateFormat.format(rating.getFeedbackDate()) : "");
            customerRatingDTOList.add(customerRatingDTO);
        }
        return customerRatingDTOList;
    }

    @Override
    public String createRating(CreateRatingDTO createRatingDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        Reservation reservation = reservationMapper.getReservationByReservationID(createRatingDTO.getReservationID());
        reservationMapper.updateReservationIsRatedById(reservation.getReservationID(), 1);

        Rating rating = new Rating();
        rating.setCustomerID(id);
        rating.setPloID(createRatingDTO.getPloID());
        rating.setReservationID(createRatingDTO.getReservationID());
        rating.setStar(createRatingDTO.getStar());
        rating.setContent(createRatingDTO.getContent());
        rating.setFeedbackDate(currentTimestamp);
        ratingMapper.sendRating(rating);
        return Message.CREATE_RATING_SUCCESS;
    }

    @Override
    public String cancelRating(int reservationID) {
        Reservation reservation = reservationMapper.getReservationByReservationID(reservationID);
        reservationMapper.updateReservationIsRatedById(reservation.getReservationID(), 2);
        return Message.CANCEL_RATING_SUCCESS;
    }

    private Integer countRecords(String ploId){
        return ratingMapper.countRecords(ploId);
    }
}
