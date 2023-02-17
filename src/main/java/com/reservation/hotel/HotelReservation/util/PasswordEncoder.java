package com.reservation.hotel.HotelReservation.util;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// This was made to try and help figure ot the adding user test case, see tests class for TODO
public class PasswordEncoder {
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
