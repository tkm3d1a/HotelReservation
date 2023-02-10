package com.reservation.hotel.HotelReservation.controller;

import com.reservation.hotel.HotelReservation.model.User;
import com.reservation.hotel.HotelReservation.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class UIController {

    @Autowired
    UserRepository userRepository;

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
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model){
        model.addAttribute("user", user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        log.info("User inserted into the hotel Database: {}", user);
        return "redirect:/login";
    }

    @GetMapping("/result")
    public String afterSignup(){
        return "result";
    }

    @GetMapping("/guestprofile")
    public String getUserProfile(@ModelAttribute User user, Model model){
        model.addAttribute("user", user);
        return "guestprofile";
    }

    @GetMapping("/admin")
    public String adminDashboard(@ModelAttribute User user, Model model){
        model.addAttribute("newEmployee", user);
        List<User> userList = userRepository.findAll();
        model.addAttribute("employeeList", userList);
        return "admin-dashboard";
    }

    @PostMapping("/admin/addNew")
    public String addClerkProfile(@ModelAttribute("user") User user){
        addNewEmployee(user);
        return "redirect:/admin";
    }

    private void addNewEmployee(User user){
        String userName = user.getFirstName().substring(0,1);
        if(user.getLastName().length() > 7){
            userName += user.getLastName().substring(0,7);
        } else {
            userName += user.getLastName();
        }
        user.setUsername(userName);
        user.setPassword(bCryptPasswordEncoder.encode("default1234"));
        user.setRole("ROLE_CLERK");
        user.setZipCode("99999");
        userRepository.save(user);
        log.info("Clicked 'addNewEmployee'");
    }
}
