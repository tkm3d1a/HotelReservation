package com.reservation.hotel.HotelReservation.Reservation;

import com.reservation.hotel.HotelReservation.hotelroom.SearchCriteria;
import com.reservation.hotel.HotelReservation.util.FormEncapsulate;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
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

        String userRole = authorities.toArray()[0].toString();
        List<Reservation> reservationsList;
        if(userRole.equals("ROLE_GUEST")){
            reservationsList = reservationService.findAllReservationsForUser(currentUser);
        } else {
            reservationsList = reservationService.findAllReservations();
        }

        reservationService.updateModifiable(reservationsList);

        model.addAttribute("allReservations", reservationsList);
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setSourceForm("reservationsPage");
        model.addAttribute("searchCriteria", searchCriteria);
        model.addAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("minCheckOutDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return "test-reservation";
    }

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

    @GetMapping("/edit/{resId}")
    public String editReservation(Model model, @PathVariable int resId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = authentication.getName();
        FormEncapsulate formFields = new FormEncapsulate();

        Reservation reservation = reservationService.findReservationByGuestIDAndReservationID(resId, currentUser);

        model.addAttribute("reservation", reservation);
        model.addAttribute("promoCode", formFields);
        model.addAttribute("minCheckInDate", reservation.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("minCheckOutDate", reservation.getStartDate().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("maxCheckInDate", reservation.getEndDate().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        model.addAttribute("maxCheckOutDate", reservation.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "edit-reservation";
    }

    @PostMapping("/edit/{resId}")
    public String postEditReservation(@ModelAttribute("reservation") Reservation reservation){
        reservationService.updateReservation(reservation);
        return "redirect:/reservation/view";
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


//    @GetMapping("/make-reservation/{roomID}")
//    //TODO: Get room loaded when making call
//    //TODO: Get date range loaded when making call
//    public String makeNewReservation(Model model, @PathVariable int roomID){
//
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUser = auth.getName();
//        Reservation reservation = new Reservation();
//
//        reservationService.addCurrentUserToRes(reservation, currentUser);
//
//        model.addAttribute("reservation", reservation);
//        return "make-reservation";
//    }

    @GetMapping("/make-reservation/{roomNumber}/in/{checkInDate}/out/{checkOutDate}")
    public String reserveRoom(@PathVariable String roomNumber, @PathVariable String checkInDate,
                              @PathVariable String checkOutDate, Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        Reservation reservation = reservationService.createNewReservation(
                roomNumber,
                currentUser,
                checkInDate,
                checkOutDate);

        model.addAttribute("reservation", reservation);
        return "make-reservation";
    }

    @PostMapping("/make-reservation/stage")
    //TODO: handle user not existing and room not existing search results
    public String stageReservation(@ModelAttribute("newReservation") Reservation newReservation){
        log.info("As it comes in: {}", newReservation);

        reservationService.findRoomForRes(newReservation);
        reservationService.updateDailyRate(newReservation, newReservation.getRoom().getBaseRate());
        reservationService.updateNumDays(newReservation);
        reservationService.updateTotalRate(newReservation);

        log.info("before saving: {}", newReservation);
        reservationService.saveReservation(newReservation);

        return "redirect:/reservation/view";
    }

//    @GetMapping("/search")
//    public String baseSearch(Model model){
//        Reservation searchDates = new Reservation();
//        model.addAttribute("searchDates", searchDates);
////        List<Reservation> searchedReservations = reservationService.findAllReservations();
////        log.info("{}", searchedReservations);
////        log.info("{}", model.asMap().get("reservationList"));
////        model.addAttribute("searchedReservations", model.asMap().get("reservationList"));
//        return "search-reservation";
//    }

//    @PostMapping("/search/submit")
//    public String searchSubmit(@ModelAttribute Reservation searchDates, RedirectAttributes redirectAttributes){
//        List<Reservation> searchedReservations = reservationService.findAllReservations();
//        redirectAttributes.addFlashAttribute("reservationList", searchedReservations);
//
//        reservationService.findReservationsBetweenDates(searchDates);
//
//        return "redirect:/reservation/search";
//    }

    @GetMapping("/confirm")
    public String confirmReservationView(Model model){
        log.info("{}", model);
        Reservation passedReservation = (Reservation) model.asMap().get("reservationStage");
        FormEncapsulate formFields = new FormEncapsulate();
        model.addAttribute("reservation", passedReservation);
        model.addAttribute("promoCode", formFields);
        return "confirm-reservation";
    }

    @PostMapping("/confirm/stage/{resID}")
    public String stageConfirmReservation(@PathVariable int resID, RedirectAttributes redirectAttributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        Reservation reservation = reservationService.findReservationByGuestIDAndReservationID(resID, currentUser);
        redirectAttributes.addFlashAttribute("reservationStage", reservation);
        return "redirect:/reservation/confirm";
    }

    @PostMapping("/confirm/{resID}")
    public String confirmReservation(@PathVariable int resID){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUser = auth.getName();

        reservationService.confirmRoom(resID, currentUser);

        return "redirect:/reservation/view";
    }

    @PostMapping("/apply-promo")
    public String applyPromo(@ModelAttribute("promoCode" ) FormEncapsulate promoCode,
                             @ModelAttribute("reservation") Reservation passedReservation,
                             RedirectAttributes redirectAttributes)
    {
        Reservation reservation = reservationService.findReservationByID(passedReservation.getId());
        reservationService.applyPromo(promoCode.getFormString(), reservation.getId());

        redirectAttributes.addFlashAttribute("reservationStage", reservation);

        if(reservation.isConfirmed()){
            return "redirect:/reservation/edit/" + reservation.getId();
        } else {
            return "redirect:/reservation/confirm";
        }
    }

    @DeleteMapping("/delete/{resID}")
    public String cancelReservation(@PathVariable int resID){
        reservationService.cancelReservation(resID);
        return "redirect:/reservation/view";
    }
}
