package com.project.Eparking.service.impl;

import com.project.Eparking.dao.*;
import com.project.Eparking.domain.*;
import com.project.Eparking.domain.dto.*;
import com.project.Eparking.domain.Image;
import com.project.Eparking.domain.ParkingMethod;
import com.project.Eparking.domain.ReservationMethod;
import com.project.Eparking.domain.response.Page;
import com.project.Eparking.service.interf.ParkingLotOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ParkingLotOwnerServiceImpl implements ParkingLotOwnerService {

    private final ParkingLotOwnerMapper parkingLotOwnerMapper;

    private final ImageMapper imageMapper;

    private final ParkingMethodMapper parkingMethodMapper;

    private final ReservationMethodMapper reservationMethodMapper;

    private final ParkingStatusMapper parkingStatusMapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Page<ListPloDTO> getPloByParkingStatus(int status, int pageNum, int pageSize) {
        List<ListPloDTO> parkingLotOwnerDTOList = new ArrayList<>();
        List<PLO> ploList;
        int pageNumOffset = pageNum == 0 ? 0 : (pageNum -1) * pageSize;
        List<Integer> parkingStatus = null;
        //1. Get list PLO by status from database
        if (status == 0){
            parkingStatus = List.of(3,4,5,6);
        }

        if (status == 1){
            parkingStatus = List.of(3,4,5);
        }

        if (status == 2){
            parkingStatus = List.of(6);
        }

        ploList = parkingLotOwnerMapper.getListPloByParkingStatusWithPagination(parkingStatus,pageNumOffset, pageSize, "");
        if (ploList.isEmpty()){
            return new Page<ListPloDTO>(parkingLotOwnerDTOList, pageNum, pageSize, 0);
        }
        int countRecords = this.countRecords(parkingStatus, "");

        //2. Mapping data from entity to dto
        for (PLO plo : ploList){
            ParkingStatus parkingStatusEntity = parkingStatusMapper.getByParkingStatusId(plo.getParkingStatusID());
            ListPloDTO ploDto = new ListPloDTO();
            ploDto.setPloID(plo.getPloID());
            ploDto.setAddress(plo.getAddress());
            ploDto.setPhoneNumber(plo.getPhoneNumber());
            ploDto.setParkingName(plo.getParkingName());
            ploDto.setFullName(plo.getFullName());
            ploDto.setStatusName(parkingStatusEntity.getStatusName());
            ploDto.setContractDuration(Objects.nonNull(plo.getContractDuration()) ?
                    dateFormat.format(plo.getContractDuration()) : "");
            parkingLotOwnerDTOList.add(ploDto);
        }

        return new Page<ListPloDTO>(parkingLotOwnerDTOList, pageNum, pageSize, countRecords);
    }

    @Override
    public Page<RegistrationHistoryDTO> getListRegistrationHistory(int status, int pageNum, int pageSize, String keywords) {
        List<RegistrationHistoryDTO> parkingLotOwnerDTOList = new ArrayList<>();
        List<PLO> ploList;
        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        // 1. Get list Registration by parking status from database
        List<Integer> parkingStatus = List.of(status);
        String searchKeyword = keywords.trim();
        ploList = parkingLotOwnerMapper.getListPloByParkingStatusWithPagination(parkingStatus, pageNumOffset, pageSize, searchKeyword);
        if (ploList.isEmpty()){
            return new Page<RegistrationHistoryDTO>(parkingLotOwnerDTOList, pageNum, pageSize, 0);
        }

        // 2. Mapping data from entity to dto
        for (PLO plo : ploList) {
            ParkingStatus parkingStatusEntity = parkingStatusMapper.getByParkingStatusId(plo.getParkingStatusID());
            RegistrationHistoryDTO ploDto = new RegistrationHistoryDTO(plo.getPloID(), plo.getFullName(), plo.getPhoneNumber(),
                    plo.getAddress(), plo.getParkingName());
            ploDto.setRegisterContract(Objects.nonNull(plo.getRegisterContract()) ?
                    dateFormat.format(plo.getRegisterContract()) : "");
            ploDto.setStatusName(parkingStatusEntity.getStatusName());
            ploDto.setContractDuration(Objects.nonNull(plo.getContractDuration()) ?
                    dateFormat.format(plo.getContractDuration()) : "");
            ploDto.setBrowseContract(Objects.nonNull(plo.getBrowseContract()) ?
                    dateFormat.format(plo.getBrowseContract()) : "");
            parkingLotOwnerDTOList.add(ploDto);
        }
        int countRecords = this.countRecords(parkingStatus, searchKeyword);
        return new Page<RegistrationHistoryDTO>(parkingLotOwnerDTOList, pageNum, pageSize, countRecords);

    }

    @Override
    public PloRegistrationDTO getPloRegistrationByPloId(String ploId) {
        //1. Get Image by ploId
        List<Image> images = imageMapper.getImageByPloId(ploId);

        //2. Get Plo by ploId;
        PLO ploEntity = parkingLotOwnerMapper.getPloById(ploId);
        if (Objects.isNull(ploEntity)) {
            return null;
        }

        List<ImageDTO> imageDTOS = new ArrayList<>();
        //3. Mapping to dto
        if (!images.isEmpty()) {
            for (Image image : images) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageID(image.getImageID());
                imageDTO.setImageLink(image.getImageLink());
                imageDTOS.add(imageDTO);
            }
        }

        PloRegistrationDTO parkingLotOwnerDTO = new PloRegistrationDTO();
        parkingLotOwnerDTO.setPloID(ploEntity.getPloID());
        parkingLotOwnerDTO.setParkingName(ploEntity.getParkingName());
        parkingLotOwnerDTO.setAddress(ploEntity.getAddress());
        parkingLotOwnerDTO.setPhoneNumber(ploEntity.getPhoneNumber());
        parkingLotOwnerDTO.setEmail(ploEntity.getEmail());
        parkingLotOwnerDTO.setContractDuration(Objects.nonNull(ploEntity.getContractDuration()) ?
                dateFormat.format(ploEntity.getContractDuration()) : "");
        parkingLotOwnerDTO.setContractLink(ploEntity.getContractLink());
        parkingLotOwnerDTO.setStatus(ploEntity.getStatus());
        parkingLotOwnerDTO.setSlot(ploEntity.getSlot());
        parkingLotOwnerDTO.setLatitude(ploEntity.getLatitude() != null ? ploEntity.getLatitude().toString() : "");
        parkingLotOwnerDTO.setLongtitude(ploEntity.getLongtitude() != null ? ploEntity.getLongtitude().toString() : "");
        parkingLotOwnerDTO.setLength(ploEntity.getLength() != null ? ploEntity.getLength() : 0);
        parkingLotOwnerDTO.setWidth(ploEntity.getWidth() != null ? ploEntity.getWidth() : 0);
        parkingLotOwnerDTO.setStar(ploEntity.getStar());
        parkingLotOwnerDTO.setCurrentSlot(ploEntity.getCurrentSlot());
        parkingLotOwnerDTO.setBalance(ploEntity.getBalance());
        parkingLotOwnerDTO.setFullName(ploEntity.getFullName());
        parkingLotOwnerDTO.setPassword(ploEntity.getPassword());
        parkingLotOwnerDTO.setIdentify(ploEntity.getIdentify());
        parkingLotOwnerDTO.setParkingStatusID(ploEntity.getParkingStatusID());
        parkingLotOwnerDTO.setRole(ploEntity.getRole());
        parkingLotOwnerDTO.setWaitingTime(ploEntity.getWaitingTime());
        parkingLotOwnerDTO.setRegisterContract(Objects.nonNull(ploEntity.getRegisterContract()) ?
                dateFormat.format(ploEntity.getRegisterContract()) : "");
        parkingLotOwnerDTO.setBrowseContract(Objects.nonNull(ploEntity.getBrowseContract()) ?
                dateFormat.format(ploEntity.getBrowseContract()) : "");
        parkingLotOwnerDTO.setDescription(ploEntity.getDescription());
        parkingLotOwnerDTO.setCancelBookingTime(ploEntity.getCancelBookingTime());
        parkingLotOwnerDTO.setImages(imageDTOS);
        return parkingLotOwnerDTO;
    }

    public Page<ListPloDTO> getListRegistrationByParkingStatus(int status, int pageNum, int pageSize, String keywords) {
        List<ListPloDTO> parkingLotOwnerDTOList = new ArrayList<>();
        List<PLO> ploList;
        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        // 1. Get list Registration by parking status from database
        List<Integer> parkingStatus = List.of(status );
        String searchKeyword =keywords.trim();
        ploList = parkingLotOwnerMapper.getListPloByParkingStatusWithPagination(parkingStatus, pageNumOffset, pageSize, searchKeyword);
        if (ploList.isEmpty()){
            return new Page<ListPloDTO>(parkingLotOwnerDTOList, pageNum, pageSize, 0);
        }

        // 2. Mapping data from entity to dto
        for (PLO plo : ploList) {
            ParkingStatus parkingStatusEntity = parkingStatusMapper.getByParkingStatusId(plo.getParkingStatusID());
            ListPloRegistrationDTO ploDto = new ListPloRegistrationDTO(plo.getPloID(), plo.getFullName(), plo.getPhoneNumber(),
                    plo.getAddress(), plo.getParkingName());
            ploDto.setRegisterContract(Objects.nonNull(plo.getRegisterContract()) ?
                    dateFormat.format(plo.getRegisterContract()) : "");
            ploDto.setStatusName(parkingStatusEntity.getStatusName());
            ploDto.setContractDuration(Objects.nonNull(plo.getContractDuration()) ?
                    dateFormat.format(plo.getContractDuration()) : "");
            parkingLotOwnerDTOList.add(ploDto);
        }
        int countRecords = this.countRecords(parkingStatus, searchKeyword);
        return new Page<ListPloDTO>(parkingLotOwnerDTOList, pageNum, pageSize, countRecords);
    }

    public boolean updatePloStatusById(UpdatePloStatusDTO updatePloStatusDTO) {
        //1. Check is parking lot owner is exist
        boolean isSuccess = true;
        PLO plo = parkingLotOwnerMapper.getPloById(updatePloStatusDTO.getPloId());
        if (Objects.isNull(plo)){
            isSuccess = false;
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        //* plus month by contractionDuration request
        Timestamp newContractionDuration = Timestamp.valueOf(currentTimestamp
                .toLocalDateTime().plusMonths(updatePloStatusDTO.getContractDuration()));

        //2. Update parking status by plo id
        plo.setParkingStatusID(updatePloStatusDTO.getNewStatus());
        plo.setContractDuration(newContractionDuration);
        plo.setBrowseContract(currentTimestamp);
        plo.setContractLink(updatePloStatusDTO.getContractLink());
//        plo.setLongtitude(updatePloStatusDTO.getLongtitude());
//        plo.setLatitude(updatePloStatusDTO.getLatitude());
        parkingLotOwnerMapper.updateParkingStatusByPloId(plo);

        return isSuccess;
    }

    @Override
    public Page<ListPloDTO> getListPloByKeywords(String keyword, int status, int pageNum, int pageSize) {
        List<ListPloDTO> listPloDTOS = new ArrayList<>();
        int pageNumOffset = pageNum == 0 ? 0 : (pageNum - 1) * pageSize;
        List<Integer> parkingStatus = null;
        //1. Get list plo entity by keyword
        if (status == 0){
            parkingStatus = List.of(3,4,5,6);
        }

        if (status == 1){
            parkingStatus = List.of(3,4,5);
        }

        if (status == 2){
            parkingStatus = List.of(6);
        }
        String searchKeyword = keyword.trim();
        List<PLO> ploList = parkingLotOwnerMapper.getListPloByKeywordsWithPagination(searchKeyword,
                parkingStatus, pageNumOffset, pageSize);

        if (ploList.isEmpty()){
            return new Page<ListPloDTO>(listPloDTOS, pageNum, pageSize, 0);
        }

        //2. Mapping data to dto
        for (PLO plo : ploList) {
            ParkingStatus parkingStatusEntity = parkingStatusMapper.getByParkingStatusId(plo.getParkingStatusID());
            ListPloDTO listPloDTO = new ListPloDTO();
            listPloDTO.setPloID(plo.getPloID());
            listPloDTO.setFullName(plo.getFullName());
            listPloDTO.setParkingName(plo.getParkingName());
            listPloDTO.setAddress(plo.getAddress());
            listPloDTO.setPhoneNumber(plo.getPhoneNumber());
            listPloDTO.setStatusName(parkingStatusEntity.getStatusName());
            listPloDTO.setContractDuration(Objects.nonNull(plo.getContractDuration()) ?
                    dateFormat.format(plo.getContractDuration()) : "");
            listPloDTOS.add(listPloDTO);
        }

        //3. get total page
        int totalRecords = this.countRecords(parkingStatus, searchKeyword);
        return new Page<ListPloDTO>(listPloDTOS, pageNum, pageSize, totalRecords);
    }

    @Override
    public ParkingLotOwnerDTO getDetailPloById(String ploId) throws Exception {
        //1. Get Image by ploId
        List<Image> images = imageMapper.getImageByPloId(ploId);

        //3. Get Plo by ploId;
        PLO ploEntity = parkingLotOwnerMapper.getPloById(ploId);
        if (Objects.isNull(ploEntity)){
            return null;
        }

        //4. Get Fee by parking method
        List<ParkingMethod> parkingMethod = parkingMethodMapper.getParkingMethodById(ploId);
        ParkingMethod morningMethod = new ParkingMethod();
        ParkingMethod eveningMethod = new ParkingMethod();
        ParkingMethod overnightMethod = new ParkingMethod();

        List<ReservationMethod> reservationMethods = reservationMethodMapper.getAllReservationMethod();
        for (ReservationMethod reservationMethod : reservationMethods){
            if (reservationMethod.getMethodName().equals("Morning")){
                morningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodName().equals("Evening")){
                eveningMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }

            if (reservationMethod.getMethodName().equals("Overnight")){
                overnightMethod =
                        parkingMethod.stream().
                                filter(t -> t.getMethodID() == reservationMethod.getMethodID())
                                .findFirst().orElse(null);
            }
        }
        
        List<ImageDTO> imageDTOS = new ArrayList<>();
        //4. Mapping to dto
        if (!images.isEmpty()){
            for (Image image : images){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setImageID(image.getImageID());
                imageDTO.setImageLink(image.getImageLink());
                imageDTOS.add(imageDTO);
            }
        }

        ParkingLotOwnerDTO parkingLotOwnerDTO = new ParkingLotOwnerDTO();
        parkingLotOwnerDTO.setPloID(ploEntity.getPloID());
        parkingLotOwnerDTO.setFullName(ploEntity.getFullName());
        parkingLotOwnerDTO.setParkingName(ploEntity.getParkingName());
        parkingLotOwnerDTO.setAddress(ploEntity.getAddress());
        parkingLotOwnerDTO.setPhoneNumber(ploEntity.getPhoneNumber());
        parkingLotOwnerDTO.setEmail(ploEntity.getEmail());
        parkingLotOwnerDTO.setContractLink(ploEntity.getContractLink());
        parkingLotOwnerDTO.setStatus(ploEntity.getStatus());
        parkingLotOwnerDTO.setMorningFee(morningMethod != null ? morningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setEveningFee(eveningMethod != null ? eveningMethod.getPrice() : 0);
        parkingLotOwnerDTO.setOverNightFee(overnightMethod != null ? overnightMethod.getPrice() : 0);
        parkingLotOwnerDTO.setSlot(ploEntity.getSlot());
        parkingLotOwnerDTO.setLatitude(ploEntity.getLatitude() != null ? ploEntity.getLatitude() .toString() : "");
        parkingLotOwnerDTO.setLongtitude(ploEntity.getLongtitude() != null ? ploEntity.getLongtitude().toString(): "");
        parkingLotOwnerDTO.setLength(ploEntity.getLength() != null ? ploEntity.getLength() : 0);
        parkingLotOwnerDTO.setWidth(ploEntity.getWidth() != null ? ploEntity.getWidth() : 0);
        parkingLotOwnerDTO.setStar(ploEntity.getStar());
        parkingLotOwnerDTO.setCurrentSlot(ploEntity.getCurrentSlot());
        parkingLotOwnerDTO.setImages(imageDTOS);
        parkingLotOwnerDTO.setParkingStatusID(ploEntity.getParkingStatusID());
        parkingLotOwnerDTO.setBrowseContract(Objects.nonNull(ploEntity.getBrowseContract())?
                dateFormat.format(ploEntity.getBrowseContract()) : "");
        parkingLotOwnerDTO.setContractDuration(Objects.nonNull(ploEntity.getContractDuration())?
                dateFormat.format(ploEntity.getContractDuration()) : "");
        return parkingLotOwnerDTO;
    }

    private Integer countRecords(List<Integer> parkingStatus, String keywords){
        return parkingLotOwnerMapper.countRecords(parkingStatus, keywords);
    }
}
