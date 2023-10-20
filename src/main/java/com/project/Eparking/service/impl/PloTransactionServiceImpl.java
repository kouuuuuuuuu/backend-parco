package com.project.Eparking.service.impl;

import com.project.Eparking.dao.ParkingLotOwnerMapper;
import com.project.Eparking.dao.TransactionMapper;
import com.project.Eparking.dao.TransactionMethodMapper;
import com.project.Eparking.domain.PLO;
import com.project.Eparking.domain.PLOTransaction;
import com.project.Eparking.domain.TransactionMethod;
import com.project.Eparking.domain.dto.PloWithdrawalDTO;
import com.project.Eparking.domain.dto.WithdrawalTransactionMethodDTO;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.service.interf.PloTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    public Page<PloWithdrawalDTO> getListWithdrawalByStatus(int status, int pageNum, int pageSize) {
        List<PloWithdrawalDTO> ploWithdrawalDTOS = new ArrayList<>();


        //1. Get list ploTransaction by status
        List<Integer> withdrawalStatus = null;
        if (status == 1 ){
            withdrawalStatus = List.of(2);
        }

        if (status == 2){
            withdrawalStatus = List.of(3,4);
        }

        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        List<PLOTransaction> listPloTransaction = transactionMapper.getPagePloTransactionByStatus(withdrawalStatus, pageNumOffset, pageSize);

        if (listPloTransaction.isEmpty()){
            return new Page<PloWithdrawalDTO>(ploWithdrawalDTOS, pageNum, pageSize, 0);
        }

        //2. Mapping data to DTO
        for (PLOTransaction ploTransaction : listPloTransaction){
            List<WithdrawalTransactionMethodDTO> transactionMethodDTOS = new ArrayList<>();
            //2.1 Get plo by ploId
            PLO plo = parkingLotOwnerMapper.getPloById(ploTransaction.getPloID());

            //2.2 Get transaction method by history id
            List<TransactionMethod> transactionMethods = transactionMethodMapper.getListTransactionMethodByHistoryId(ploTransaction.getHistoryID());

            PloWithdrawalDTO ploWithdrawalDTO = new PloWithdrawalDTO();
            ploWithdrawalDTO.setTransactionID(ploTransaction.getHistoryID());
            ploWithdrawalDTO.setPloID(plo.getPloID());
            ploWithdrawalDTO.setStatusName((ploTransaction.getStatus() == 2) ?
                    "Chờ phê duyệt" : (ploTransaction.getStatus() == 3)? "Chấp nhận" : "Từ chối");
            ploWithdrawalDTO.setAddress(plo.getAddress());
            ploWithdrawalDTO.setParkingName(plo.getParkingName());
            ploWithdrawalDTO.setFullName(plo.getFullName());
            ploWithdrawalDTO.setDepositAmount(ploTransaction.getDepositAmount());
            ploWithdrawalDTO.setBalance(plo.getBalance());
            ploWithdrawalDTO.setTransactionDate(Objects.nonNull(ploTransaction.getTransactionDate())?
                    dateFormat.format(ploTransaction.getTransactionDate()) : "");
            ploWithdrawalDTO.setTransactionResultDate(Objects.nonNull(ploTransaction.getTransactionResultDate())?
                    dateFormat.format(ploTransaction.getTransactionResultDate()) : "");
            ploWithdrawalDTO.setPhoneNumber(plo.getPhoneNumber());

            for (TransactionMethod transactionMethod : transactionMethods){
                WithdrawalTransactionMethodDTO transactionMethodDTO = new WithdrawalTransactionMethodDTO();
                transactionMethodDTO.setBankName(transactionMethod.getBankName());
                transactionMethodDTO.setBankNumber(transactionMethod.getBankNumber());
                transactionMethodDTOS.add(transactionMethodDTO);
            }
            ploWithdrawalDTO.setTransactionMethod(transactionMethodDTOS);
            ploWithdrawalDTOS.add(ploWithdrawalDTO);
        }
        int totalRecords = this.countRecords(withdrawalStatus, "");
        return new Page<PloWithdrawalDTO>(ploWithdrawalDTOS, pageNum, pageSize, totalRecords);
    }

    @Override
    public boolean updateWithdrawalStatus(int transactionId, int status) {
        boolean isSuccess = true;

        //1. Get ploTransaction by transactionId
        PLOTransaction ploTransaction = transactionMapper.getPloTransactionByHistoryId(transactionId);
        if (Objects.isNull(ploTransaction)){
            return false;
        }

        if (status == 3){
            //* If status = 3 -> Update plo balance
            PLO plo = parkingLotOwnerMapper.getPloById(ploTransaction.getPloID());
            double newBalance = plo.getBalance() - ploTransaction.getDepositAmount();
            parkingLotOwnerMapper.updatePloBalanceById(plo.getPloID(), newBalance);
        }

        transactionMapper.updatePloTransactionStatusByHistoryId(transactionId, status);

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
        String searchKeyword = "%" + keywords.trim() + "%";
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
