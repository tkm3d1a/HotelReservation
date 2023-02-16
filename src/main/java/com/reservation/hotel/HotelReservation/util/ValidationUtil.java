package com.reservation.hotel.HotelReservation.util;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomRepository;
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

    @Autowired
    RoomRepository roomRepository;

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

    public boolean checkIfUserNameAlreadyExistsInDB(String newUserName){
        List<HotelUser> allExistingUsers = hotelUserRepository.findAll();
        for (HotelUser user:allExistingUsers) {
            if(user.getUsername().equals(newUserName)){
                log.info("username: {} already exists", newUserName);
                return true;
            }
        }
        return false;
    }

    public boolean checkIfEmailAlreadyExistsInDB(String newEmail){
        List<HotelUser> allExistingUsers = hotelUserRepository.findAll();
        for (HotelUser user:allExistingUsers) {
            if(user.getEmail().equals(newEmail)){
                log.info("Email: {} already exists", newEmail);
                return true;
            }
        }
        return false;
    }

    public boolean checkIfRoomNumberAlreadyExistsInDB(int newRoomNumber){
        List<Room> allExistingRooms = roomRepository.findAll();
        for (Room room:allExistingRooms ) {
            if(newRoomNumber == room.getRoomNumber()) {
                log.info("Room Number {} already exists", newRoomNumber);
                return true;
            }
        }
        return false;
    }
}
