package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/profile")
public class HotelUserController {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @GetMapping("")
//    @RolesAllowed("ROLE_GUEST") //TODO: Investigate RolesAllowed as another security method
    public String getUserProfile(Model model){
        //TODO: Apply auth retrieval of username to all places where it can be done
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        HotelUser hotelUser = getUserInfo(username);
        model.addAttribute("hotelUser", hotelUser);
        return "view-profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        HotelUser hotelUser = hotelUserRepository.findByUsername(username);
        model.addAttribute("hotelUser", hotelUser);
        log.info("Viewing user: {}", hotelUser);
        return "edit-profile";
    }

    @PostMapping("/edit-profile")
//    @RolesAllowed("")
    public String updateProfile(@ModelAttribute HotelUser modelHotelUser, RedirectAttributes redirectAttributes){
        //TODO: Migrate to HotelUserService
        HotelUser dbHotelUser = hotelUserRepository.findByUsername(modelHotelUser.getUsername());
        log.info("Updating user: {}", dbHotelUser);
        if(dbHotelUser.getRole().equals("ROLE_GUEST")){
            dbHotelUser.setFirstName(modelHotelUser.getFirstName());
            dbHotelUser.setLastName(modelHotelUser.getLastName());
        }
        dbHotelUser.setPhoneNumber(modelHotelUser.getPhoneNumber());
        dbHotelUser.setStreetAddress(modelHotelUser.getStreetAddress());
        dbHotelUser.setCity(modelHotelUser.getCity());
        dbHotelUser.setState(modelHotelUser.getState());
        dbHotelUser.setZipCode(modelHotelUser.getZipCode());
        log.info("Updated user: {}", dbHotelUser);
        hotelUserRepository.save(dbHotelUser);
        redirectAttributes.addAttribute("username", dbHotelUser.getUsername());
        //TODO: manage which profile returns to (clerk vs employee)
        if(dbHotelUser.getRole().equals("ROLE_CLERK")){
            return "redirect:/profile";
        } else {
            return "redirect:/profile";
        }
    }

    private HotelUser getUserInfo(String username) {
        HotelUser hotelUser = new HotelUser();
        if(username == null){
            hotelUser.setUsername("FakeTest");
            hotelUser.setEmail("FakeEmail@fake.com");
            hotelUser.setZipCode("00000");
            hotelUser.setFirstName("Fake");
            hotelUser.setLastName("Test");
            hotelUser.setStreetAddress("999 Fake rd");
            hotelUser.setCity("Fake");
            hotelUser.setState("TST");
        } else {
            hotelUser = hotelUserRepository.findByUsername(username);
        }

        return hotelUser;
    }
}
