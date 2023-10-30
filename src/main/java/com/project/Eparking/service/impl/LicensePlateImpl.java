package com.project.Eparking.service.impl;

import com.project.Eparking.constant.Message;
import com.project.Eparking.dao.LicensePlateMapper;
import com.project.Eparking.dao.ReservationMapper;
import com.project.Eparking.domain.LicensePlate;
import com.project.Eparking.domain.Reservation;
import com.project.Eparking.exception.ApiRequestException;
import com.project.Eparking.service.interf.LicensePlateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LicensePlateImpl implements LicensePlateService {
    private final LicensePlateMapper licensePlateMapper;
    private final ReservationMapper reservationMapper;

//    private static final String LICENSE_PLATE_PATTERN = "^[1-9]{1}[0-9]{1}[A-Z]{1}[0-9]{1}-[0-9]{3}\\.[0-9]{2}$";


    @Override
    public List<LicensePlate> getListLicensePlateByCustomerID(String customerID) {
        try {
            return licensePlateMapper.getListLicensePlateByCustomerID(customerID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get list license plate by customerID" +e.getMessage());
        }
    }

    @Override
    public List<LicensePlate> getListLicensePlate() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        return licensePlateMapper.getListLicensePlateByCustomerID(id);
    }

    @Override
    public boolean deleteLicensePlateByLicensePlateID(int licensePlateID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        boolean isDeleteSuccess = true;
        List<Integer> licensePlatesId = licensePlateMapper.getListLicensePlateByCustomerID(id)
                .stream().map(LicensePlate::getLicensePlateID).collect(Collectors.toList());
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
                int isDelete = licensePlateMapper.deleteLicensePlate(String.valueOf(licensePlateID), id);
                isDeleteSuccess = isDelete != 0;
            }else {
                isDeleteSuccess = false;
            }
        }

        return  isDeleteSuccess;
    }

    @Override
    public String addLicensePlate(String licensePlate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        List<String> licensePlates = licensePlateMapper.getAllLicensePlateByCustomerId(id)
                .stream().map(LicensePlate::getLicensePlate).collect(Collectors.toList());
        List<String> cleanLicensePlates = licensePlates.stream().map(t -> t.replaceAll("[-.]","")).collect(Collectors.toList());
        String cleanLicensePlate = licensePlate.replaceAll("[-.]","");
        String message = "";
        if (!cleanLicensePlates.contains(cleanLicensePlate)){
            licensePlateMapper.createLicensePlate(licensePlate, id);
            return Message.ADD_LICENSE_PLATE_SUCCESS;
        }

        for (int i = 0; i < cleanLicensePlates.size(); i++){
            if (cleanLicensePlates.get(i).contains(cleanLicensePlate)){
                LicensePlate licensePlatesEntity = licensePlateMapper.
                        getLicensePlateByLicensePlate(licensePlates.get(i), id);

                //** If this licensePlate have deleted -> update isDelete = 0
                if (licensePlatesEntity.isDelete()){
                    licensePlateMapper.updateLicensesPlateStatusById(licensePlatesEntity.getLicensePlateID(), id);
                    message =  Message.ADD_LICENSE_PLATE_SUCCESS;
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
