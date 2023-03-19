package com.reservation.hotel.HotelReservation.reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomService;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReservationService {

    @Resource
    private ReservationRepository reservationRepository;

    @Resource
    private HotelUserService hotelUserService;

    @Resource
    private RoomService roomService;

    public void updateDailyRate(Reservation resToUpdate, int updatedRate){
        //TODO: update with logic to clamp rate to a base value
        resToUpdate.setDailyRate(updatedRate);
    }

    //Do not remove, may need for setting up res by employee
    public void findGuestForRes(Reservation resToUpdate){
        int findGuest = resToUpdate.getGuest().getId();
        HotelUser guest = hotelUserService.findUserByID(findGuest);
        String role = guest.getRole();
        if(role.equals("ROLE_GUEST")){
            resToUpdate.setGuest(guest);
        } else {
            log.warn("Attempted to assign reservation to non-guest user");
            log.warn("Reservation was not updated");
            //TODO: How to signal as an error?
        }
    }

    public void addCurrentUserToRes(Reservation resToUpdate, String currentUser){
        HotelUser user = hotelUserService.findUserByUsername(currentUser);
        String role = user.getRole();
        if(role.equals("ROLE_GUEST")){
            resToUpdate.setGuest(user);
        } else {
            log.warn("Attempted to assign reservation to non-guest user");
            log.warn("Reservation was not updated");
            //TODO: How to signal as an error?
        }
    }

    public void findRoomForRes(Reservation resToUpdate){
        int findRoom = resToUpdate.getRoom().getId();
        Room room = roomService.findRoomByID(findRoom);
        resToUpdate.setRoom(room);
    }

    public void saveReservation(Reservation resToUpdate){
        reservationRepository.save(resToUpdate);
    }

    public List<Reservation> findAllReservationsForUser(String currentUser){
        HotelUser user = hotelUserService.findUserByUsername(currentUser);
        return reservationRepository.findAllByGuest_Id(user.getId());
    }
}
