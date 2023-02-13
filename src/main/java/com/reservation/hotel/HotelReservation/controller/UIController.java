package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class UIController {

    @Autowired
    HotelUserRepository hotelUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/")
    public String getMessage(){
//        if(request != null){
//            Principal principal = request.getUserPrincipal();
//            log.info("{}", principal.getName());
//        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        log.info("{}, Roles: {}", currentPrincipalName, authorities);
        return "index";
    }

    @GetMapping("/login")
    public String loginUser(){
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("hotelUser", new HotelUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute HotelUser hotelUser, RedirectAttributes redirectAttributes){
        hotelUser.setPassword(bCryptPasswordEncoder.encode(hotelUser.getPassword()));
        hotelUserRepository.save(hotelUser);
        log.info("User inserted: {}", hotelUser);
        //TODO: Right now, spring-security captures and redirects to login -> update to follow correct redirect path
        //      on new register
        redirectAttributes.addAttribute("username", hotelUser.getUsername());
        return "redirect:/guest-profile";
    }

    @GetMapping("/result")
    public String afterSignup(){
        return "result";
    }

    @PostMapping("/result")
    public String resultTest(){
        return "result";
    }

    /*
    Profile related endpoints
     */
    @GetMapping("/guest-profile")
//    @RolesAllowed("ROLE_GUEST") //TODO: Investigate RolesAllowed as another security method
    public String getUserProfile(Model model){
        //TODO: Apply auth retrieval of username to all places where it can be done
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        HotelUser hotelUser = getUserInfo(username);
        //TODO: fix redirect and merge into one profile page
        if(!hotelUser.getRole().equals("ROLE_GUEST")){
            return "redirect:/clerk-profile";
        }
        model.addAttribute("hotelUser", hotelUser);
        return "guest-profile";
    }

    @GetMapping("/clerk-profile")
    public String getClerkProfile(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        HotelUser hotelUser = getUserInfo(username);
        model.addAttribute("hotelUser", hotelUser);
        return "clerk-profile";
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
            return "redirect:/clerk-profile";
        } else {
            return "redirect:/guest-profile";
        }
    }

    @GetMapping("/admin")
    public String adminDashboard(@ModelAttribute HotelUser hotelUser, Model model){
        model.addAttribute("newEmployee", hotelUser);
        List<HotelUser> hotelUserList = hotelUserRepository.findAll();
        hotelUserList.removeIf(hotelUserLoop -> hotelUserLoop.getRole().equals("ROLE_GUEST"));
        model.addAttribute("employeeList", hotelUserList);
        return "admin-dashboard";
    }

    @PostMapping("/admin/addNew")
    public String addClerkProfile(@ModelAttribute("user") HotelUser hotelUser){
        addNewEmployee(hotelUser);
        return "redirect:/admin";
    }

    private void addNewEmployee(HotelUser hotelUser){
        log.info("Clicked 'addNewEmployee'");
        String userName = hotelUser.getFirstName().substring(0,1);
        if(hotelUser.getLastName().length() > 7){
            userName += hotelUser.getLastName().substring(0,7);
        } else {
            userName += hotelUser.getLastName();
        }
        hotelUser.setUsername(userName);
        hotelUser.setPassword(bCryptPasswordEncoder.encode("default1234"));
        hotelUser.setRole("ROLE_CLERK");
        hotelUser.setZipCode("99999");
        hotelUserRepository.save(hotelUser);
        log.info("Added: {}", hotelUser);
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
