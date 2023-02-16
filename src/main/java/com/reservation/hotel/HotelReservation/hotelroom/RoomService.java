package com.reservation.hotel.HotelReservation.hotelroom;

import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    ValidationUtil validationUtil;

    public boolean addNewRoom(Room newRoom) {
        log.info("Clicked 'addNewRoom'");
        if (!validationUtil.checkIfRoomNumberAlreadyExistsInDB(newRoom.getRoomNumber())) {
            log.info("Room to be added: {}", newRoom);
            roomRepository.save(newRoom);
            log.info("Room {} added", newRoom.getRoomNumber());
            return true;
        } else {
            return false;
        }
    }

}
