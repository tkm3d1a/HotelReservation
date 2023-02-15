package com.reservation.hotel.HotelReservation.hoteluser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequestMapping("/profile")
public class HotelUserController {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public String updateProfile(@ModelAttribute HotelUser modelHotelUser){
        //TODO: Migrate to HotelUserService
        HotelUser dbHotelUser = hotelUserRepository.findByUsername(modelHotelUser.getUsername());
        if(dbHotelUser.getRole().equals("ROLE_GUEST")){
            dbHotelUser.setFirstName(modelHotelUser.getFirstName());
            dbHotelUser.setLastName(modelHotelUser.getLastName());
        }
        String newPassword = modelHotelUser.getPassword();
        if(!newPassword.isBlank()){
            dbHotelUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        }
        dbHotelUser.setPhoneNumber(modelHotelUser.getPhoneNumber());
        dbHotelUser.setStreetAddress(modelHotelUser.getStreetAddress());
        dbHotelUser.setCity(modelHotelUser.getCity());
        dbHotelUser.setState(modelHotelUser.getState());
        dbHotelUser.setZipCode(modelHotelUser.getZipCode());
        if(!isHotelUserSame(modelHotelUser,dbHotelUser)){
            log.info("Updating user: {}", dbHotelUser);
            hotelUserRepository.save(dbHotelUser);
            log.info("Updated user: {}", dbHotelUser);
        } else {
            log.info("User did not update profile information");
        }

        return "redirect:/profile";
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

    private boolean isHotelUserSame(HotelUser modelUser, HotelUser dbUser){
        return modelUser.getFirstName() == dbUser.getFirstName()
                && modelUser.getLastName() == dbUser.getLastName()
                && modelUser.getStreetAddress() == dbUser.getStreetAddress()
                && modelUser.getCity() == dbUser.getCity()
                && modelUser.getState() == dbUser.getState()
                && modelUser.getZipCode() == dbUser.getZipCode()
                && modelUser.getPhoneNumber() == dbUser.getPhoneNumber()
                && modelUser.getPassword().isBlank();
    }
}
