package com.reservation.hotel.HotelReservation.config;

import com.reservation.hotel.HotelReservation.service.HotelUserDetailsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{

    @Resource
    private HotelUserDetailsService hotelUserDetailsService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/guestprofile").hasAnyRole("GUEST", "ROLE_GUEST")
                .requestMatchers("/", "/login")
                .permitAll()
                .anyRequest()
                .authenticated());


        http.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler));
        http.httpBasic();
        http.csrf().disable();
        return (SecurityFilterChain)http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

}
