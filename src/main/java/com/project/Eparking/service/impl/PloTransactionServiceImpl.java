package com.project.Eparking.service.impl;

import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.dto.PloWithdrawalDTO;
import com.project.Eparking.domain.dto.WithdrawalTransactionMethodDTO;
import com.project.Eparking.domain.request.PushNotificationRequest;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.PushNotificationService;
import com.project.Eparking.service.interf.PloTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PloTransactionServiceImpl implements PloTransactionService {

    private final TransactionMethodMapper transactionMethodMapper;

    private final ParkingLotOwnerMapper parkingLotOwnerMapper;

    private final TransactionMapper transactionMapper;
    private final PushNotificationService pushNotificationService;
    private final FirebaseTokenMapper tokenMapper;
    private final UserMapper userMapper;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public boolean updateWithdrawalStatus(int transactionId, int status) {
        boolean isSuccess = true;

        //1. Get ploTransaction by transactionId
        PLOTransaction ploTransaction = transactionMapper.getPloTransactionByHistoryId(transactionId);
        if (Objects.isNull(ploTransaction)){
            return false;
        }
        PLO plo = parkingLotOwnerMapper.getPloById(ploTransaction.getPloID());
        if (status == 3){
            //* If status = 3 -> Update plo balance

            double newBalance = plo.getBalance() - ploTransaction.getDepositAmount();
            parkingLotOwnerMapper.updatePloBalanceById(plo.getPloID(), newBalance);
        }
        Timestamp transactionResultDate = new Timestamp(System.currentTimeMillis());
        transactionMapper.updatePloTransactionStatusByHistoryId(transactionId, status, transactionResultDate);

        //send noti
        PushNotificationRequest request = new PushNotificationRequest();
        request.setImage("https://fiftyfifty.b-cdn.net/eparking/Logo.png?fbclid=IwAR0Cp0mqjcD5-DCi9DvSSomsni8_gA-tg14f2GskVlpIYReh-tagSlOrb-4");
        if(status == 3){
            request.setMessage("Đơn rút tiền của bạn đã được duyệt");
        }
        if(status == 4){
            request.setMessage("Đơn rút tiền của bạn đã bị từ chối");
        }
        request.setTitle("Thông báo trạng thái của đơn rút tiền");
        request.setTopic("Thông báo trạng thái của đơn rút tiền");
        List<FirebaseToken> firebaseTokens = tokenMapper.getTokenByID(plo.getPloID());
        if(firebaseTokens==null){
            throw new ApiRequestException("Failed to get firebaseTokens");
        }
        for (FirebaseToken token:
                firebaseTokens) {
            request.setToken(token.getDeviceToken());
            pushNotificationService.sendPushNotificationToToken(request);
        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String id = authentication.getName();
//
//        //
//        Notifications notifications = new Notifications();
//        notifications.setRecipient_type("PLO");
//        notifications.setRecipient_id(plo.getPloID());
//        notifications.setSender_type("ADMIN");
//        notifications.setSender_id(id);
//        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//        notifications.setCreated_at(currentTime);
//        if(status == 3){
//            notifications.setContent("Đơn rút tiền của bạn đã được duyệt");
//        }
//        if(status == 4){
//            notifications.setContent("Đơn rút tiền của bạn đã bị từ chối");
//        }
//        userMapper.insertNotification(notifications);
        //
        return isSuccess;
    }

    @Override
    public Page<PloWithdrawalDTO> searchWithdrawalByKeyword(int status,String keywords, int pageNum, int pageSize) {
        List<PloWithdrawalDTO> ploWithdrawalDTOS = new ArrayList<>();

        //1. Search withdrawal by keyword
        List<Integer> withdrawalStatus = null;
        if (status == 1 ){
            withdrawalStatus = List.of(2);
        }

        if (status == 2){
            withdrawalStatus = List.of(3,4);
        }

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        String searchKeyword =keywords.trim();
        List<PLOTransaction> ploTransactions = transactionMapper.searchPloTransactionByKeyword(withdrawalStatus, searchKeyword, pageNumOffset, pageSize);
        if (ploTransactions.isEmpty()){
            return new Page<PloWithdrawalDTO>(ploWithdrawalDTOS, pageNum, pageSize, 0);
        }

        //2. Mapping data to DTO
        for (PLOTransaction ploTransaction : ploTransactions) {
            List<WithdrawalTransactionMethodDTO> transactionMethodDTOS = new ArrayList<>();
            //2.1 Get plo by ploId
            PLO plo = parkingLotOwnerMapper.getPloById(ploTransaction.getPloID());

            //2.2 Get transaction method by history id
            List<TransactionMethod> transactionMethods = transactionMethodMapper.getListTransactionMethodByHistoryId(ploTransaction.getHistoryID());

            PloWithdrawalDTO ploWithdrawalDTO = new PloWithdrawalDTO();
            ploWithdrawalDTO.setTransactionID(ploTransaction.getHistoryID());
            ploWithdrawalDTO.setPloID(plo.getPloID());
            ploWithdrawalDTO.setStatusName((ploTransaction.getStatus() == 2) ?
                    "Chờ phê duyệt" : (ploTransaction.getStatus() == 3) ? "Chấp nhận" : "Từ chối");
            ploWithdrawalDTO.setAddress(plo.getAddress());
            ploWithdrawalDTO.setParkingName(plo.getParkingName());
            ploWithdrawalDTO.setFullName(plo.getFullName());
            ploWithdrawalDTO.setDepositAmount(ploTransaction.getDepositAmount());
            ploWithdrawalDTO.setBalance(plo.getBalance());
            ploWithdrawalDTO.setTransactionDate(Objects.nonNull(ploTransaction.getTransactionDate()) ?
                    dateFormat.format(ploTransaction.getTransactionDate()) : "");
            ploWithdrawalDTO.setTransactionResultDate(Objects.nonNull(ploTransaction.getTransactionResultDate()) ?
                    dateFormat.format(ploTransaction.getTransactionResultDate()) : "");
            ploWithdrawalDTO.setPhoneNumber(plo.getPhoneNumber());

            for (TransactionMethod transactionMethod : transactionMethods) {
                WithdrawalTransactionMethodDTO transactionMethodDTO = new WithdrawalTransactionMethodDTO();
                transactionMethodDTO.setBankCode(transactionMethod.getBankCode());
                transactionMethodDTO.setBankName(transactionMethod.getBankName());
                transactionMethodDTO.setBankNumber(transactionMethod.getBankNumber());
                transactionMethodDTOS.add(transactionMethodDTO);
            }
            ploWithdrawalDTO.setTransactionMethod(transactionMethodDTOS);
            ploWithdrawalDTOS.add(ploWithdrawalDTO);
        }

        int totalRecords = this.countRecords(withdrawalStatus, searchKeyword);
        return new Page<PloWithdrawalDTO>(ploWithdrawalDTOS, pageNum, pageSize, totalRecords);
    }

    private Integer countRecords(List<Integer> parkingStatus, String keywords){
        return transactionMapper.countRecords(parkingStatus, keywords);
    }
}
