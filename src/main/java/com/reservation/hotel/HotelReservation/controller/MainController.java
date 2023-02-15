package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.List;

@Slf4j
@Controller
public class MainController {

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
        //TODO: I think for the user registering problem this should be moved to hotelUserService and do the business logic checking there
        hotelUserRepository.save(hotelUser);
        log.info("User inserted: {}", hotelUser);
        //TODO: Right now, spring-security captures and redirects to login -> update to follow correct redirect path
        //      on new register
        redirectAttributes.addAttribute("username", hotelUser.getUsername());
        return "redirect:/profile";
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
    Admin related endpoints
     */
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
}
