package com.project.Eparking.service.impl;

import com.project.Eparking.constant.Message;
import com.project.Eparking.dao.MotorbikeMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.domain.Motorbike;
import com.project.Eparking.domain.Reservation;
import com.project.Eparking.domain.dto.CreateMotorbikeDTO;
import com.project.Eparking.domain.dto.MotorbikeDTO;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.MotorbikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MotorbikeServiceImpl implements MotorbikeService {
    private final MotorbikeMapper motorbikeMapper;
    private final ReservationMapper reservationMapper;

//    private static final String LICENSE_PLATE_PATTERN = "^[1-9]{1}[0-9]{1}[A-Z]{1}[0-9]{1}-[0-9]{3}\\.[0-9]{2}$";


    @Override
    public List<Motorbike> getListLicensePlateByCustomerID(String customerID) {
        try {
            return motorbikeMapper.getListLicensePlateByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get list license plate by customerID" +e.getMessage());
        }
    }

    @Override
    public List<MotorbikeDTO> getListLicensePlate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<MotorbikeDTO> motorbikeDTOS = new ArrayList<>();
        List<Motorbike> motorbikes = motorbikeMapper.getListLicensePlateByCustomerID(id);
        if (motorbikes.isEmpty()){
            return motorbikeDTOS;
        }
        for (Motorbike motorbike : motorbikes){
            MotorbikeDTO motorbikeDTO = new MotorbikeDTO();
            motorbikeDTO.setMotorbikeID(motorbike.getLicensePlateID());
            motorbikeDTO.setLicensePlate(motorbike.getLicensePlate());
            motorbikeDTO.setMotorbikeName(motorbike.getMotorbikeName());
            motorbikeDTO.setMotorbikeColor(motorbike.getMotorbikeColor());
            motorbikeDTOS.add(motorbikeDTO);
        }
        return motorbikeDTOS;
    }

    @Override
    public boolean deleteLicensePlateByLicensePlateID(int licensePlateID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        boolean isDeleteSuccess = true;
        List<Integer> licensePlatesId = motorbikeMapper.getListLicensePlateByCustomerID(id)
                .stream().map(Motorbike::getLicensePlateID).collect(Collectors.toList());
        if(!licensePlatesId.contains(licensePlateID)){
            isDeleteSuccess = false;
        }else {

            List<Reservation> reservations = reservationMapper.getReservationByLicensesPlateId(licensePlateID);
            boolean isBooking = false;
            for (Reservation reservation : reservations){
                if (reservation.getStatusID() == 1 || reservation.getStatusID() == 2 || reservation.getStatusID() == 3){
                    isBooking = true;
                    break;
                }
            }
            if (!isBooking){
                int isDelete = motorbikeMapper.deleteLicensePlate(licensePlateID, id);
                isDeleteSuccess = isDelete != 0;
            }else {
                isDeleteSuccess = false;
            }
        }

        return  isDeleteSuccess;
    }

    @Override
    public String addLicensePlate(CreateMotorbikeDTO motorbikeDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<String> licensePlates = motorbikeMapper.getListLicensePlate()
                .stream().map(Motorbike::getLicensePlate).collect(Collectors.toList());
        List<String> cleanLicensePlates = licensePlates.stream().map(t -> t.replaceAll("[-.]","")).collect(Collectors.toList());
        String cleanLicensePlate = motorbikeDTO.getLicensePlate().replaceAll("[-.]","");
        String message = "";
        if (!cleanLicensePlates.contains(cleanLicensePlate)){
            Motorbike motorbike = new Motorbike();
            motorbike.setLicensePlate(motorbikeDTO.getLicensePlate());
            motorbike.setMotorbikeName(motorbikeDTO.getMotorbikeName());
            motorbike.setMotorbikeColor(motorbikeDTO.getMotorbikeColor());
            motorbikeMapper.createLicensePlate(motorbike, id);
            return Message.ADD_LICENSE_PLATE_SUCCESS;
        }

        for (int i = 0; i < cleanLicensePlates.size(); i++){
            if (cleanLicensePlates.get(i).contains(cleanLicensePlate)){
                Motorbike licensePlatesEntity = motorbikeMapper.
                        getLicensePlateByLicensePlateString(licensePlates.get(i));

                //** If this licensePlate have deleted -> update isDelete = 0
                if (licensePlatesEntity.isDelete() && !id.contains(licensePlatesEntity.getCustomerID())){
                    Motorbike motorbike = new Motorbike();
                    motorbike.setLicensePlate(motorbikeDTO.getLicensePlate());
                    motorbike.setMotorbikeName(motorbikeDTO.getMotorbikeName());
                    motorbike.setMotorbikeColor(motorbikeDTO.getMotorbikeColor());
                    motorbikeMapper.createLicensePlate(motorbike, id);
                    message = Message.ADD_LICENSE_PLATE_SUCCESS;
                    break;
                }else {
                    message = Message.DUPLICATE_LICENSE_PLATE;
                    break;
                }
            }
        }
        return message;
    }
}
