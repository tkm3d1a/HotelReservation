package com.reservation.hotel.HotelReservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {
    @GetMapping("/")
    public String getMessage(){
        return "index";
    }

    @GetMapping("/login")
    public String loginUser(){
        return "login";
    }
}
