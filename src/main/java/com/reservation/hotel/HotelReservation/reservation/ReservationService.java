package com.reservation.hotel.HotelReservation.reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomService;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

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

    public void updateNumDays(Reservation reservation){
        int numDays = (int) ChronoUnit.DAYS.between(reservation.getStartDate(), reservation.getEndDate());
        reservation.setNumDays(numDays);
    }

    public void updateTotalRate(Reservation reservation){
        int totalRate = reservation.getDailyRate() * reservation.getNumDays();
        reservation.setTotalRate(totalRate);
    }

    public void confirmRoom(int resID, String currentUser){
        Optional<Reservation> reservationOptional = reservationRepository.findById(resID);
        Reservation reservation;
        if(reservationOptional.isPresent()){
            reservation = reservationOptional.get();
            if(reservation.getGuest().getUsername().equals(currentUser)){
                reservation.setConfirmed(true);
                saveReservation(reservation);
            } else {
                log.warn("Reservation does not match logged in user");
            }
        } else {
            log.warn("No reservation found with that ID");
        }
        //TODO: update availability table?
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

    public void cancelReservation(int resID){
        reservationRepository.deleteById(resID);
    }

    public List<Reservation> findAllReservationsForUser(String currentUser){
        HotelUser user = hotelUserService.findUserByUsername(currentUser);
        return reservationRepository.findAllByGuest_Id(user.getId());
    }

    public List<Reservation> findAllReservationsByGuestID(int guestID){
       return reservationRepository.findAllByGuest_Id(guestID);
    }

    public  List<Reservation> findAllReservations(){
        return reservationRepository.findAll();
    }

//    public List<Reservation> findReservationsBetweenDates(Reservation searchDates){
//        HashSet<Reservation> reservationHashSet = new HashSet<>();
//        HashSet<Room> roomHashSet = new HashSet<>();
//
//        List<Reservation> result1 = reservationRepository.findAllByStartDateBetween(searchDates.getStartDate(), searchDates.getEndDate());
//        log.info("Result 1...");
//        for( Reservation foundReservation : result1){
//            log.info("{}", foundReservation);
//            reservationHashSet.add(foundReservation);
//            roomHashSet.add(foundReservation.getRoom());
//        }
//
//        List<Reservation> result2 = reservationRepository.findAllByEndDateBetween(searchDates.getStartDate(), searchDates.getEndDate());
//        log.info("Result 2...");
//        for( Reservation foundReservation : result2){
//            log.info("{}", foundReservation);
//            reservationHashSet.add(foundReservation);
//            roomHashSet.add(foundReservation.getRoom());
//        }
//
//        List<Reservation> finalReservationsAvailable = new ArrayList<>(reservationHashSet.size());
//        log.info("Final Reservation HashSet...");
//        for( Reservation inHashSet : reservationHashSet){
//            log.info("{}", inHashSet);
//            finalReservationsAvailable.add(inHashSet);
//        }
//
//        log.info("Final Room HashSet...");
//        for( Room inHashSet : roomHashSet){
//            log.info("{}", inHashSet);
//        }
//
//        return finalReservationsAvailable;
//    }

    public Reservation findReservationByGuestIDAndReservationID(int resID, String currentUser){
        Optional<Reservation> reservationOptional = reservationRepository.findById(resID);
        Reservation reservation = new Reservation();
        if(reservationOptional.isPresent()){
            reservation = reservationOptional.get();
            if(reservation.getGuest().getUsername().equals(currentUser)){
                return reservation;
            } else {
                log.warn("Reservation does not match logged in user");
            }
            log.warn("No reservation found with that ID");
        }

        return reservation;
    }

    public Reservation findreservationByID(int resID){
        Optional<Reservation> reservationOptional = reservationRepository.findById(resID);
        return reservationOptional.orElseGet(Reservation::new);
    }
}
