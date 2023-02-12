package com.reservation.hotel.HotelReservation.config;

import com.reservation.hotel.HotelReservation.hoteluser.HotelUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    @Resource
    private HotelUserService hotelUserService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
//                .requestMatchers("/guestprofile").hasAnyRole("ROLE_GUEST")
//                .requestMatchers("/admin").hasAnyRole("ROLE_CLERK")
                .requestMatchers("/", "/login", "/register/**", "/result", "/admin/**", "/edit-profile/**")
                .permitAll()
                .anyRequest()
                .authenticated());


        http.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler));
        http.httpBasic();
        http.csrf().disable();
        return (SecurityFilterChain)http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
