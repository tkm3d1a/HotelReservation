package com.reservation.hotel.HotelReservation.hoteluser;

import com.reservation.hotel.HotelReservation.util.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Controller
@RequestMapping("/profile")
public class HotelUserController {

    @Resource
    HotelUserRepository hotelUserRepository;

    @Resource
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    ValidationUtil validationUtil;

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
        log.info("Updating user: {}", dbHotelUser);
        String newPassword = modelHotelUser.getPassword();
        if(!newPassword.isBlank()){
            log.info("Password updated");
            dbHotelUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        } else {
            log.info("Password not changed");
            modelHotelUser.setPassword(dbHotelUser.getPassword());
        }
        if(!validationUtil.checkIfHotelUsersAreSame(modelHotelUser,dbHotelUser)){
            dbHotelUser.setFirstName(modelHotelUser.getFirstName());
            dbHotelUser.setLastName(modelHotelUser.getLastName());
            dbHotelUser.setPhoneNumber(modelHotelUser.getPhoneNumber());
            dbHotelUser.setStreetAddress(modelHotelUser.getStreetAddress());
            dbHotelUser.setCity(modelHotelUser.getCity());
            dbHotelUser.setState(modelHotelUser.getState());
            String enteredZipCode = modelHotelUser.getZipCode();
            if(enteredZipCode.length() == 5){
                dbHotelUser.setZipCode(enteredZipCode);
            } else {
                //TODO: this can probably be removed after more testing
                log.error("attempted to update zipcode with non zipcode number");
                return "redirect:/profile/edit-profile?error=";
            }
            hotelUserRepository.save(dbHotelUser);
            log.info("Updated user information: {}", dbHotelUser);
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
}
