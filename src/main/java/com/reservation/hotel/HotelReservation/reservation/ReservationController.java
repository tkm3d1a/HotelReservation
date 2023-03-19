package com.reservation.hotel.HotelReservation.reservation;

import com.reservation.hotel.HotelReservation.hotelroom.Room;
import com.reservation.hotel.HotelReservation.hotelroom.RoomService;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ReservationService reservationService;

    @Autowired
    HotelUserService hotelUserService;

    @Autowired
    RoomService roomService;

    //TODO: should this move to service or stay here?
    @GetMapping("/view")
    public String getAllReservations(Model model){
        List<Reservation> allReservations = reservationRepository.findAll();
        model.addAttribute("allReservations", allReservations);
        return "test-reservation";
    }

    @GetMapping("/view/{guest_id}")
    public String getGuestReservations(Model model, @PathVariable int guest_id){
        List<Reservation> guestReservations = reservationRepository.findAllByGuest_Id(guest_id);
        model.addAttribute("allReservations", guestReservations);
        return "test-reservation";
    }

    @GetMapping("/make-reservation")
    //TODO: Get room loaded when making call
    //TODO: Get date range loaded when making call
    //TODO: Get guest loaded when making call
    public String makeNewReservation(Model model){
        Reservation reservation = new Reservation();
        reservation.setConfirmed(false);
        reservation.setDailyRate(99);
        model.addAttribute("reservation", reservation);
        return "make-reservation";
    }

    @PostMapping("/make-reservation/stage")
    //TODO: handle user not existing and room not existing search results
    public String stageReservation(@ModelAttribute("newReservation") Reservation newReservation){
        log.info("As it comes in: {}", newReservation);

        //TODO: break out to function?
        int findGuest = newReservation.getGuest().getId();
        HotelUser guest = hotelUserService.findUserByID(findGuest);
        newReservation.setGuest(guest);
        log.info("After finding guest: {}", newReservation);

        //TODO: break out to function?
        int findRoom = newReservation.getRoom().getId();
        Room room = roomService.findRoomByID(findRoom);
        newReservation.setRoom(room);
        log.info("After finding room: {}", newReservation);

        reservationService.updateDailyRate(newReservation, room.getBaseRate());
        log.info("After updating rate: {}", newReservation);

        reservationRepository.save(newReservation);

        return "redirect:/reservation/view";
    }
}
