package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUser;
import com.reservation.hotel.HotelReservation.hoteluser.HotelUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String registerUser(@ModelAttribute HotelUser hotelUser, Model model){
        model.addAttribute("user", hotelUser);
        hotelUser.setPassword(bCryptPasswordEncoder.encode(hotelUser.getPassword()));
        hotelUserRepository.save(hotelUser);
        log.info("User inserted into the hotel Database: {}", hotelUser);
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

    @GetMapping("/guest-profile")
    public String getUserProfile(@RequestParam(name = "username", required = false) String username, Model model){
        HotelUser hotelUser = getUserInfo(username);
        model.addAttribute("hotelUser", hotelUser);
        return "guest-profile";
    }

    @GetMapping("/clerk-profile")
    public String getClerkProfile(@RequestParam(name = "username", required = false) String username, Model model){
        HotelUser hotelUser = getUserInfo(username);
        model.addAttribute("hotelUser", hotelUser);
        return "clerk-profile";
    }

    @GetMapping("/edit-profile")
    public String editProfile(@RequestParam(name = "username") String username, Model model){
        HotelUser hotelUser = hotelUserRepository.findByUsername(username);
        model.addAttribute("hotelUser", hotelUser);
        return "edit-profile";
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
