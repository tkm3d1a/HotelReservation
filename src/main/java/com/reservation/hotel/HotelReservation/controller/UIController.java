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
}
