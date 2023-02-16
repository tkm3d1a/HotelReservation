package com.reservation.hotel.HotelReservation.util;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ValidationUtil {

    @Autowired
    HotelUserRepository hotelUserRepository;

    public boolean checkIfHotelUsersAreSame(HotelUser modelUser, HotelUser dbUser){
        Boolean isHotelUserSame = modelUser.getFirstName().equals(dbUser.getFirstName())
                && modelUser.getLastName().equals(dbUser.getLastName())
                && modelUser.getStreetAddress().equals(dbUser.getStreetAddress())
                && modelUser.getCity().equals(dbUser.getCity())
                && modelUser.getState().equals(dbUser.getState())
                && modelUser.getZipCode().equals(dbUser.getZipCode())
                && modelUser.getPhoneNumber().equals(dbUser.getPhoneNumber())
                && modelUser.getPassword() .equals(dbUser.getPassword());

        return isHotelUserSame;
    }

    public boolean checkIfUserNameAndEmailAlreadyExistsInDB(String newUserName, String newEmail){
        List<HotelUser> allExistingUsers = hotelUserRepository.findAll();
        for (HotelUser user:allExistingUsers) {
            if(user.getUsername().equals(newUserName) || user.getEmail().equals(newEmail)){
                log.info("username: {} and/or Email: {} already exists", newUserName, newEmail);
                return true;
            }
        }
        return false;
    }
}
