package com.reservation.hotel.HotelReservation.reservation;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    ReservationRepository reservationRepository;

    @Resource
    ReservationService reservationService;

    //TODO: should this move to service or stay here?
    @GetMapping("/view")
    public String getAllReservations(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info(authorities.toArray()[0].toString());

        String userRole = authorities.toArray()[0].toString();
        List<Reservation> reservationsList;
        if(userRole.equals("ROLE_GUEST")){
            log.info("Guest is logged in. Viewing only guest reservations");
            reservationsList = reservationService.findAllReservationsForUser(currentUser);
        } else {
            reservationsList = reservationService.findAllReservations();
        }

        model.addAttribute("allReservations", reservationsList);
        return "test-reservation";
    }

    //TODO: Clean up/remove /view/user mapping
//    @GetMapping("/view/user")
//    public String getUserReservations(Model model){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUser = auth.getName();
//
//        List<Reservation> currentUserReservations = reservationService.findAllReservationsForUser(currentUser);
//        model.addAttribute("allReservations", currentUserReservations);
//        return "test-reservation";
//    }

    @GetMapping("/view/{guest_id}")
    public String getGuestReservations(Model model, @PathVariable int guest_id){
        List<Reservation> guestReservations = reservationService.findAllReservationsByGuestID(guest_id);
        model.addAttribute("allReservations", guestReservations);
        return "test-reservation";
    }

    @GetMapping("/view/confirmed")
    public String getGuestReservations(Model model){
        List<Reservation> confirmedReservations = reservationRepository.findAllByIsConfirmed(true);
        model.addAttribute("allReservations", confirmedReservations);
        return "test-reservation";
    }

    @GetMapping("/make-reservation")
    //TODO: Get room loaded when making call
    //TODO: Get date range loaded when making call
    //TODO: Get guest loaded when making call
    public String makeNewReservation(Model model){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();
        Reservation reservation = new Reservation();

        reservationService.addCurrentUserToRes(reservation, currentUser);

        model.addAttribute("reservation", reservation);
        return "make-reservation";
    }

    @PostMapping("/make-reservation/stage")
    //TODO: handle user not existing and room not existing search results
    public String stageReservation(@ModelAttribute("newReservation") Reservation newReservation){
        log.info("As it comes in: {}", newReservation);

        reservationService.findRoomForRes(newReservation);
        log.info("After finding room: {}", newReservation);

        reservationService.updateDailyRate(newReservation, newReservation.getRoom().getBaseRate());
        log.info("After updating rate: {}", newReservation);

        reservationService.saveReservation(newReservation);

        return "redirect:/reservation/view";
    }

    @GetMapping("/search")
    public String baseSearch(Model model){
        Reservation searchDates = new Reservation();
        model.addAttribute("searchDates", searchDates);
//        List<Reservation> searchedReservations = reservationService.findAllReservations();
//        log.info("{}", searchedReservations);
//        log.info("{}", model.asMap().get("reservationList"));
//        model.addAttribute("searchedReservations", model.asMap().get("reservationList"));
        return "search-reservation";
    }

    @PostMapping("/search/submit")
    public String searchSubmit(@ModelAttribute Reservation searchDates, RedirectAttributes redirectAttributes){
        List<Reservation> searchedReservations = reservationService.findAllReservations();
        redirectAttributes.addFlashAttribute("reservationList", searchedReservations);

        reservationService.findReservationsBetweenDates(searchDates);

        return "redirect:/reservation/search";
    }

    @PostMapping("/confirm")
    public String confirmReservation(@ModelAttribute Reservation reservation){
        reservationService.confirmRoom(reservation);
        return "redirect:/reservation/view";
    }
}
