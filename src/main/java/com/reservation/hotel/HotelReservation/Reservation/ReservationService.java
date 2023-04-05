package com.reservation.hotel.HotelReservation.Reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomService;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
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

    public void applyPromo(String promoCode, int resID){
        Optional<Reservation> reservationOptional = reservationRepository.findById(resID);
        Reservation reservation;

        if(reservationOptional.isPresent()){
            reservation = reservationOptional.get();
            if(promoCode.equals("1234")){
                updateDailyRate(reservation, 1);
                //TODO change setPromoApplied to service function
                reservation.setPromoApplied(true);
                updateTotalRate(reservation);
                saveReservation(reservation);
            } else {
                log.warn("Entered promo code is not valid");
            }
        } else {
            log.warn("No reservation found matching ResID {}", resID);
        }
    }

    public void updateReservation(Reservation reservation){
        Reservation updatedReservation = findReservationByID(reservation.getId());
        updatedReservation.setStartDate(reservation.getStartDate());
        updatedReservation.setEndDate(reservation.getEndDate());
        updateNumDays(updatedReservation);
        updateTotalRate(updatedReservation);
        saveReservation(updatedReservation);
    }

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
        log.info("{}", room);
        resToUpdate.setRoom(room);
    }

    public void saveReservation(Reservation resToUpdate){
        reservationRepository.save(resToUpdate);
    }

    public void cancelReservation(int resID){
        reservationRepository.deleteById(resID);
    }

    public void updateModifiable(List<Reservation> reservationList){
        LocalDate currentDate = LocalDate.now();
        int compareTo;
        for(Reservation reservation : reservationList){
            compareTo = currentDate.compareTo(reservation.getStartDate());
            if(compareTo > 0 && reservation.isNotStarted()){
                reservation.setNotStarted(false);
                saveReservation(reservation);
                log.info("Updated Reservation {} to started", reservation.getId());
            }
        }
    }

    public void sortReservationsByDate(List<Reservation> reservationList){
        reservationList.sort(Comparator.comparing(Reservation::getStartDate));
    }

    public List<Reservation> findAllReservationsForUser(String currentUser){
        HotelUser user = hotelUserService.findUserByUsername(currentUser);
        List<Reservation> reservations = reservationRepository.findAllByGuest_Id(user.getId());
        sortReservationsByDate(reservations);
        return reservations;
    }

    public List<Reservation> findAllReservationsByGuestID(int guestID){
        List<Reservation> reservations = reservationRepository.findAllByGuest_Id(guestID);
        sortReservationsByDate(reservations);
        return reservations;
    }

    public  List<Reservation> findAllReservations(){
        List<Reservation> reservations = reservationRepository.findAll();
        sortReservationsByDate(reservations);
        return reservations;
    }

    public Reservation findReservationByGuestIDAndReservationID(int resID,
                                                                String currentUser){
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

    public Reservation findReservationByID(int resID){
        Optional<Reservation> reservationOptional = reservationRepository.findById(resID);
        return reservationOptional.orElseGet(Reservation::new);
    }

    public Reservation createNewReservation(String roomNumber,
                                            String currentUser,
                                            String checkInDate,
                                            String checkOutDate) {
        Room room = roomService.findRoomByRoomNumber(roomNumber);
        HotelUser hotelUser = hotelUserService.findUserByUsername(currentUser);
        if(!hotelUser.getRole().equals("ROLE_GUEST")) {
            throw new RuntimeException("Reservation can only be assigned to a guest");
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkIn = LocalDate.parse(checkInDate, dateTimeFormatter);
        LocalDate checkOut = LocalDate.parse(checkOutDate, dateTimeFormatter);

        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setGuest(hotelUser);
        reservation.setStartDate(checkIn);
        reservation.setEndDate(checkOut);
        updateDailyRate(reservation, room.getBaseRate());
        updateNumDays(reservation);
        updateTotalRate(reservation);

        return reservation;
    }
}